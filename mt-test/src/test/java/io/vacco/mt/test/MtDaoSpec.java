package io.vacco.mt.test;

import io.vacco.metolithe.changeset.*;
import io.vacco.metolithe.core.MtDescriptor;
import io.vacco.metolithe.core.MtLog;
import io.vacco.metolithe.dao.MtDaoMapper;
import io.vacco.metolithe.dao.MtQuery;
import io.vacco.metolithe.dao.MtWriteDao;
import io.vacco.metolithe.id.*;
import io.vacco.metolithe.util.MtPage1;
import io.vacco.metolithe.util.MtPage2;
import io.vacco.mt.test.dao.*;
import io.vacco.mt.test.schema.*;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.codejargon.fluentjdbc.api.*;
import org.junit.runner.RunWith;

import static io.vacco.shax.logging.ShArgument.kv;
import static j8spec.J8Spec.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class MtDaoSpec extends MtSpec {

  private static final String     schema = "public";
  private static final FluentJdbc jdbc = new FluentJdbcBuilder()
      .connectionProvider(ds)
      .afterQueryListener(edt -> log.info("[{}], {}", edt.success(), edt.sql()))
      .build();

  private static final MtIdFn<Integer>  m3Ifn = new MtMurmur3IFn();
  private static final MtIdFn<Long>     m3Lfn = new MtMurmur3LFn();
  private static final MtIdFn<Integer>  xxIfn = new MtXxHashIFn();

  private static List<MtTable> tables;
  private static List<MtChange> changes;

  public static String generateRandomDigits(int n) {
    var random = new Random();
    var result = new StringBuilder();
    for (int i = 0; i < n; i++) {
      result.append(random.nextInt(10));
    }
    return result.toString();
  }

  static {
    ds.setURL("jdbc:h2:file:./build/db-test;DB_CLOSE_DELAY=-1");

    describe("Schema code generation", () -> {
      it("Generates typed field DAO definitions", () -> {
        var out = new File(".", "src/main/java");
        new MtDaoMapper().mapSchema(out, "io.vacco.mt.test.dao", fmt, testSchema);
      });
      it("Generates changelogs", () -> {
        tables = new MtMapper().build(fmt, testSchema);
        log.info("Tables: {}", kv("tables", tables));
        for (var g : MtLevel.values()) {
          log.info("\n{} ================================ {}", g, g);
          changes = new MtLogMapper(schema).process(tables, g);
          for (var chg : changes) {
            log.info("\n{}", chg);
          }
        }
      });
      it("Creates a database and applies the generated change logs.", () -> {
        MtLog.setLogger(log::info);
        var ctx = "integration-test";
        changes = new MtLogMapper(schema).process(tables, MtLevel.TABLE_COMPACT);
        for (var chg : changes) {
          chg.source = MtDaoSpec.class.getCanonicalName();
          chg.context = ctx;
        }
        try (var conn = ds.getConnection()) {
          new MtApply(conn).applyChanges(changes, ctx);
        }
        var applied = changes.stream().filter(chg -> chg.state == MtState.Applied).count();
        var found = changes.stream().filter(chg -> chg.state == MtState.Found).count();
        assertTrue(applied == changes.size() || found == changes.size());
      });
    });

    var pDao = new PhoneDao(schema, fmt, jdbc, m3Ifn);

    describe("Type safe DAOs", () -> {
      it("Creates base DAOs for data access", () -> {
        var dDao = new MtWriteDao<>(schema, jdbc, new MtDescriptor<>(Device.class, fmt), m3Lfn);
        var uDao = new MtWriteDao<>(schema, jdbc, new MtDescriptor<>(DbUser.class, fmt), m3Ifn);
        var dtDao = new MtWriteDao<>(schema, jdbc, new MtDescriptor<>(DeviceTag.class, fmt), m3Lfn);
        var ufDao = new MtWriteDao<>(schema, jdbc, new MtDescriptor<>(UserFollow.class, fmt), m3Ifn);

        var stIdFn = new MtIdFn<String>() {
          @Override public String apply(Object[] objects) { return objects[0].toString(); }
          @Override public Class<String> getIdType() { return String.class; }
        };
        var urDao = new MtWriteDao<>(schema, jdbc, new MtDescriptor<>(DbUserRole.class, fmt), stIdFn);

        log.info("{}", kv("p0", pDao.save(p0)));

        var p01 = pDao.loadExisting(p0.pid);
        assertEquals(p0.pid, p01.pid);
        assertEquals(p0.countryCode, p01.countryCode);
        assertEquals(p0.number, p01.number);

        log.info("{}", kv("p1s", pDao.save(p1)));
        log.info("{}", kv("loadWhereEq", pDao.loadWhereCountryCodeEq(1)));
        log.info("{}", kv("p1Del", pDao.deleteWhereIdEq(p1.pid)));
        log.info("{}", kv("p1s", pDao.save(p1)));
        log.info("{}", kv("p0Del", pDao.delete(p0)));
        log.info("{}", kv("p0m", pDao.upsert(p0)));

        log.info("{}", kv("d0m", dDao.upsert(d0)));
        d0.type = Device.DType.IOS;
        log.info("{}", kv("d0u", dDao.upsert(d0)));
        log.info("{}", kv("d1s", dDao.upsert(d1)));

        var dt0 = new DeviceTag();
        dt0.claimTimeUtcMs = System.currentTimeMillis();
        dt0.did = d0.did;
        dt0.pid = p0.pid;
        dt0.smsCodeSignature = "U29tZSBzaWduYXR1cmUgZm9yIHRoZSBudW1iZXIgMTIzNA==";

        var dt1 = new DeviceTag();
        dt1.claimTimeUtcMs = System.currentTimeMillis();
        dt1.did = d1.did;
        dt1.pid = p1.pid;
        dt1.smsCodeSignature = "U29tZSBzaWduYXR1cmUgZm9yIHRoZSBudW1iZXIgNDU2Nw==";

        log.info("{}", kv("dt0Id", dtDao.idOf(dt0).orElseThrow()));
        log.info("{}", kv("dt0m", dtDao.upsert(dt0)));
        log.info("{}", kv("dt1m", dtDao.upsert(dt1)));

        var ur0 = new DbUserRole();
        ur0.rid = "guest";
        ur0.createdUtcMs = System.currentTimeMillis();

        log.info("{}", kv("ur0", urDao.upsert(ur0)));

        u0.tid = dt0.tid;
        u0.rid = ur0.rid;

        u1.tid = dt1.tid;
        u1.rid = ur0.rid;

        log.info("{}", kv("u0Id", uDao.idOf(u0).orElseThrow()));
        log.info("{}", kv("u0m", uDao.upsert(u0)));
        log.info("{}", kv("u1m", uDao.upsert(u1)));

        var uf0 = new UserFollow();
        uf0.fromUid = u0.uid;
        uf0.toUid = u1.uid;

        log.info("{}", kv("uf0m", ufDao.upsert(uf0)));
      });

      it("Uses generated POJO DAOs for data access", () -> {
        var ud = new DbUserDao(schema, fmt, jdbc, m3Ifn);
        log.info("{}", kv("loadWhereAliasEqJane", ud.loadWhereAliasEq("Jane")));
        log.info("{}", kv("loadWhereEmailIn", ud.loadWhereEmailIn(u0.email, u1.email)));
        log.info("{}", kv("loadWhereEmailInArray", ud.loadWhereTidIn(u0.tid, u1.tid)));
      });

      it("Uses filter predicates for basic search", () -> {
        var ud = new DbUserDao(schema, fmt, jdbc, m3Ifn);
        var emailField = ud.dsc.getField(DbUserDao.fld_email);
        var searchQuery = MtQuery.create(schema).like(emailField, "joe%");
        var searchPage = ud.loadPage1(4, false, searchQuery, DbUserDao.fld_email, null);
        log.info("{}", kv("searchPage", searchPage));
      });

      it("Can create object pages", () -> {
        var nx1 = "Some other user name";
        var p1 = MtPage1.of(2, Arrays.asList(u0, u1), nx1);
        assertEquals(2, p1.size);
        assertEquals(2, p1.items.size());
        assertEquals(nx1, p1.nx1);

        var p2 = MtPage2.of(1, Collections.singletonList(u0), u1.alias, u1.email);
        assertEquals(1, p2.size);
        assertEquals(1, p2.items.size());
        assertEquals(u1.alias, p2.nx1);
        assertEquals(u1.email, p2.nx2);
      });

      it("Can paginate over record collections", () -> {
        var r = new Random();
        var phones = IntStream.range(0, 64).mapToObj(i -> {
          var p = new Phone();
          p.countryCode = r.nextBoolean() ? 1 : r.nextBoolean() ? 2 : 3;
          p.number = generateRandomDigits(10);
          p.smsVerificationCode = r.nextBoolean() ? Integer.parseInt(generateRandomDigits(6)) : 0;
          return p;
        }).collect(Collectors.toList());
        for (var p : phones) { pDao.upsert(p); } // TODO implement batch support

        log.info("======== All phone pages ========");
        phones.clear();
        var page0 = pDao.loadPage1(16, true, null, PhoneDao.fld_number, null);
        phones.addAll(page0.items);
        while (page0.nx1 != null) {
          log.info("{}", kv("page0", page0));
          page0 = pDao.loadPage1(16, true, null, PhoneDao.fld_number, page0.nx1);
          phones.addAll(page0.items);
        }
        log.info("{}", kv("page0", page0));

        log.info("======== Verified phone pages ========");
        var pageSum = 0L;
        var smsField = pDao.dsc.getField(PhoneDao.fld_smsVerificationCode);
        var fq0 = MtQuery.create(schema).neq(smsField, 0);
        var page1 = pDao.loadPage1(4, false, fq0, PhoneDao.fld_number, null);
        pageSum += page1.size;
        while (page1.nx1 != null) {
          log.info("{}", kv("page1", page1));
          page1 = pDao.loadPage1(4, false, fq0, PhoneDao.fld_number, page1.nx1);
          pageSum += page1.size;
        }
        log.info("{}", kv("page1", page1));

        var vp = phones.stream().filter(p -> p.smsVerificationCode != 0).collect(Collectors.toList());
        var uvp = phones.stream().filter(p -> p.smsVerificationCode == 0).collect(Collectors.toList());

        assertEquals(pageSum, vp.size());

        log.info("====> All phones: {}", phones.size());
        log.info("====> Verified phones: {}", vp.size());
        log.info("====> Unverified phones: {}", uvp.size());

        assertEquals(phones.size(), vp.size() + uvp.size());

        log.info("======== Verified, country code sorted phone pages ========");
        var fq1 = MtQuery.create(schema).neq(smsField, 0);
        var page2 = pDao.loadPage2(
          4, true, fq1,
          PhoneDao.fld_countryCode, null,
          PhoneDao.fld_number, null
        );
        while (page2.nx1 != null) {
          log.info("{}", kv("page2", page2));
          page2 = pDao.loadPage2(
            4, true, fq1,
            PhoneDao.fld_countryCode, page2.nx1,
            PhoneDao.fld_number, page2.nx2
          );
        }
        log.info("{}", kv("page2", page2));
      });
    });

    it("Renders DAO queries", () -> {
      var smsField = pDao.dsc.getField(PhoneDao.fld_smsVerificationCode);

      assertEquals("t8o.smsVerificationCode IS NOT NULL", MtQuery.create(schema).isNotNull(smsField).renderFilter());
      assertEquals("t8o.smsVerificationCode IS NULL", MtQuery.create(schema).isNull(smsField).renderFilter());
      assertEquals("t8o.smsVerificationCode = :p0", MtQuery.create(schema).eq(smsField, 123).renderFilter());
      assertEquals("t8o.smsVerificationCode < :p0", MtQuery.create(schema).lt(smsField, 123).renderFilter());
      assertEquals("t8o.smsVerificationCode <= :p0", MtQuery.create(schema).lte(smsField, 123).renderFilter());
      assertEquals("t8o.smsVerificationCode > :p0", MtQuery.create(schema).gt(smsField, 123).renderFilter());
      assertEquals("t8o.smsVerificationCode >= :p0", MtQuery.create(schema).gte(smsField, 123).renderFilter());

      assertEquals(
        "t8o.smsVerificationCode IS NOT NULL AND t8o.smsVerificationCode = :p0 AND t8o.smsVerificationCode > :p1",
        MtQuery.create(schema)
          .isNotNull(smsField)
          .and().eq(smsField, 123)
          .and().gt(smsField, 0).renderFilter()
      );
      assertEquals(
        "t8o.smsVerificationCode IS NULL OR t8o.smsVerificationCode = :p0 AND t8o.smsVerificationCode > :p1",
        MtQuery.create(schema)
          .isNull(smsField)
          .or().eq(smsField, 123)
          .and().gt(smsField, 0).renderFilter()
      );
    });

    it("Renders DAO JOIN queries", () -> {
      var apiKeyDao = new ApiKeyDao(schema, fmt, jdbc, xxIfn);
      var nsDao = new NamespaceDao(schema, fmt, jdbc, xxIfn);
      var knsDao = new KeyNamespaceDao(schema, fmt, jdbc, xxIfn);
      var uDao = new DbUserDao(schema, fmt, jdbc, m3Ifn);

      var jane = uDao.loadWhereEmailEq(u0.email).iterator().next();
      var apiKey0 = apiKeyDao.upsert(ApiKey.of(jane.uid, null, "test-key", "DEADBEEF"));

      var ns0 = nsDao.upsert(Namespace.of("namespace-00", "/ns00"));
      var ns1 = nsDao.upsert(Namespace.of("namespace-01", "/ns01"));
      var ns2 = nsDao.upsert(Namespace.of("namespace-02", "/ns02"));

      knsDao.upsert(KeyNamespace.of(apiKey0.kid, ns0.nsId));
      knsDao.upsert(KeyNamespace.of(apiKey0.kid, ns1.nsId));
      knsDao.upsert(KeyNamespace.of(apiKey0.kid, ns2.nsId));

      var ps = 2;
      var flName = NamespaceDao.fld_name;
      var flUtcMs = NamespaceDao.fld_createdAtUtcMs;

      var ij = nsDao
        .query()
        .innerJoin(knsDao.dsc, nsDao.dsc)
        .eq(knsDao.fld_kid(), apiKey0.kid);
      var p1 = nsDao.loadPage1(ps, false, ij, flName, null);
      while (p1.hasNext()) {
        log.info("{}", kv("namespaces", p1.items));
        p1 = nsDao.loadPage1(ps, false, ij, flName, p1.nx1);
      }
      log.info("{}", kv("namespaces", p1.items));

      var lj = nsDao
        .query()
        .leftJoin(knsDao.dsc, nsDao.dsc)
        .eq(knsDao.fld_kid(), apiKey0.kid);
      var p2 = nsDao.loadPage2(ps, true, lj, flName, null, flUtcMs, null);
      while (p2.hasNext()) {
        log.info("{}", kv("namespaces", p2.items));
        p2 = nsDao.loadPage2(ps, true, lj, flName, p2.nx1, flUtcMs, p2.nx2);
      }
      log.info("{}", kv("namespaces", p2.items));
    });
  }

}
