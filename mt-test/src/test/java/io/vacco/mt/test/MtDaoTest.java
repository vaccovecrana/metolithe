package io.vacco.mt.test;

import io.vacco.metolithe.changeset.*;
import io.vacco.metolithe.core.*;
import io.vacco.metolithe.dao.*;
import io.vacco.metolithe.id.*;
import io.vacco.mt.test.dao.*;
import io.vacco.mt.test.schema.*;
import io.vacco.metolithe.query.MtJdbc;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.*;

import static io.vacco.shax.logging.ShArgument.kv;
import static org.junit.Assert.*;

public class MtDaoTest extends MtTest {

  private static final MtIdFn<Integer>  m3Ifn = new MtMurmur3IFn();
  private static final MtIdFn<Long>     m3Lfn = new MtMurmur3LFn();
  private static final MtIdFn<Integer>  xxIfn = new MtXxHashIFn();

  public static String generateRandomDigits(int n) {
    var random = new Random();
    var result = new StringBuilder();
    for (int i = 0; i < n; i++) {
      result.append(random.nextInt(10));
    }
    return result.toString();
  }

  public static List<MtTable> changeLogMake() {
    var tables = new MtMapper().build(fmt, testSchema);
    log.info("Tables: {}", kv("tables", tables));
    assertFalse(tables.isEmpty());
    return tables;
  }

  public static void changeLogApply(List<MtTable> tables, MtDb db, DataSource ds) throws Exception {
    var ctx = "integration-test";
    var changes = new MtLogMapper(db.schema).process(tables, db.level);
    for (var chg : changes) {
      chg.source = MtCodegenTest.class.getCanonicalName();
      chg.context = ctx;
    }
    try (var conn = ds.getConnection()) {
      new MtApply(conn, db.schema).applyChanges(changes, ctx);
    }
    var applied = changes.stream().filter(chg -> chg.state == MtState.Applied).count();
    var found = changes.stream().filter(chg -> chg.state == MtState.Found).count();
    assertTrue(applied == changes.size() || found == changes.size());
    // round 2
    try (var conn = ds.getConnection()) {
      new MtApply(conn, db.schema)
        .withTransactions(false)
        .applyChanges(changes, ctx);
    }
    found = changes.stream().filter(chg -> chg.state == MtState.Found).count();
    assertEquals(found, changes.size());
  }

  public static void daoDataAccess(MtDb db, MtJdbc jdbc) {
    var dDao = new MtWriteDao<>(db.schema, jdbc, new MtDescriptor<>(Device.class, fmt), m3Lfn);
    var uDao = new MtWriteDao<>(db.schema, jdbc, new MtDescriptor<>(DbUser.class, fmt), m3Ifn);
    var dtDao = new MtWriteDao<>(db.schema, jdbc, new MtDescriptor<>(DeviceTag.class, fmt), m3Lfn);
    var ufDao = new MtWriteDao<>(db.schema, jdbc, new MtDescriptor<>(UserFollow.class, fmt), m3Ifn);
    var pDao = new PhoneDao(db.schema, fmt, jdbc, m3Ifn);

    var stIdFn = new MtIdFn<String>() {
      @Override public String apply(Object[] objects) { return objects[0].toString(); }
      @Override public Class<String> getIdType() { return String.class; }
    };
    var urDao = new MtWriteDao<>(db.schema, jdbc, new MtDescriptor<>(DbUserRole.class, fmt), stIdFn);

    MtLog.warn("{}", kv("p0", pDao.upsert(p0)));

    var p01 = pDao.loadExisting(p0.pid);
    assertEquals(p0.pid, p01.pid);
    assertEquals(p0.countryCode, p01.countryCode);
    assertEquals(p0.number, p01.number);

    var afterTx = (Consumer<Connection>) conn -> {
      try {
        if (conn.getWarnings() != null) {
          log.info(conn.getWarnings().toString());
        }
      } catch (SQLException e) {
        log.error(e.toString(), e);
      }
    };

    pDao.sql().tx((tx, conn) -> {
      assertEquals(tx.get(), conn);
      assertEquals(tx.get(), pDao.sql().get());
      assertEquals(tx.get(), uDao.sql().get());
      var pt = new Phone();
      pt.countryCode = 44;
      pt.number = "5552226666";
      pt.smsVerificationCode = 6789;
      log.info("{}", kv("pts", pDao.upsert(pt)));
      log.info("{}", kv("p1s", pDao.upsert(p1)));
      log.info("{}", kv("ptDel", pDao.deleteWhereIdEq(pt.pid)));
      log.info("{}", kv("pts", pDao.save(pt)));
      log.info("{}", kv("ptDel", pDao.deleteWhereIdEq(pt.pid)));
    }, afterTx);

    pDao.sql().tx((tx, conn) -> {
      pDao.upsert(p0);
      tx.rollback();
    }, afterTx);

    log.info("{}", kv("loadWhereEq", pDao.loadWhereCountryCodeEq(1)));
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

    MtLog.debug("{}", kv("ur0", urDao.upsert(ur0)));

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
  }

  public static void daoPojoAccess(MtDb db, MtJdbc jdbc) {
    var ud = new DbUserDao(db.schema, fmt, jdbc, m3Ifn);
    log.info("{}", kv("loadWhereAliasEqJane", ud.loadWhereAliasEq("Jane")));
    log.info("{}", kv("loadWhereEmailIn", ud.loadWhereEmailIn(u0.email, u1.email)));
    log.info("{}", kv("loadWhereEmailInArray", ud.loadWhereTidIn(u0.tid, u1.tid)));
    log.info("{}", kv("loadWhereEmailIn", ud.loadWhereEmailIn(
      "m00@me.com", "m01@me.com", "m02@me.com", "m03@me.com",
      "m04@me.com", "m05@me.com", "m06@me.com", "m07@me.com",
      "m08@me.com", "m09@me.com", "m10@me.com", "m11@me.com",
      "m12@me.com", "m13@me.com", "m14@me.com", "m15@me.com"
    )));
  }

  public static void daoPredicates(MtDb db, MtJdbc jdbc) {
    var ud = new DbUserDao(db.schema, fmt, jdbc, m3Ifn);
    var emailField = ud.dsc.getField(DbUserDao.fld_email);
    var searchQuery = ud.query().like(emailField, "joe%").limit(4);
    var searchPage = ud.loadPage1(searchQuery, DbUserDao.fld_email, null);
    log.info("{}", kv("searchPage", searchPage));
  }

  public static void daoPagination(MtDb db, MtJdbc jdbc) throws SQLException {
    var pDao = new PhoneDao(db.schema, fmt, jdbc, m3Ifn);
    var r = new Random();
    var phones = IntStream.range(0, 64).mapToObj(i -> {
      var p = new Phone();
      p.countryCode = r.nextBoolean() ? 1 : r.nextBoolean() ? 2 : 3;
      p.number = generateRandomDigits(10);
      p.smsVerificationCode = r.nextBoolean() ? Integer.parseInt(generateRandomDigits(6)) : 0;
      return p;
    }).collect(Collectors.toList());

    pDao.sql().batch(results -> {
      for (var p : phones) {
        results.add(pDao.saveLater(p));
      }
    });

    log.info("=========== All phones ==========");
    log.info("{}", kv("allPhones", pDao.listWhereCountryCodeIn(1)));

    log.info("======== All phone pages ========");
    phones.clear();
    var page0 =  pDao.loadPage1(pDao.query().limit(16).reverse(), PhoneDao.fld_number, null);
    phones.addAll(page0.items);
    while (page0.nx1 != null) {
      log.info("{}", kv("page0", page0));
      page0 = pDao.loadPage1(pDao.query().limit(16).reverse(), PhoneDao.fld_number, page0.nx1);
      phones.addAll(page0.items);
    }
    log.info("{}", kv("page0", page0));

    log.info("======== Verified phone pages ========");
    var pageSum = 0L;
    var smsField = pDao.dsc.getField(PhoneDao.fld_smsVerificationCode);
    var fq0 = pDao.query().neq(smsField, 0).limit(4);
    var page1 = pDao.loadPage1(fq0, PhoneDao.fld_number, null);
    pageSum += page1.size;
    while (page1.nx1 != null) {
      log.info("{}", kv("page1", page1));
      page1 = pDao.loadPage1(fq0, PhoneDao.fld_number, page1.nx1);
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
    var page2 = pDao.loadPage2(
      pDao.query().neq(smsField, 0).limit(4).reverse(),
      PhoneDao.fld_countryCode, null,
      PhoneDao.fld_number, null
    );
    while (page2.nx1 != null) {
      log.info("{}", kv("page2", page2));
      page2 = pDao.loadPage2(
        pDao.query().neq(smsField, 0).limit(4).reverse(),
        PhoneDao.fld_countryCode, page2.nx1,
        PhoneDao.fld_number, page2.nx2
      );
    }
    log.info("{}", kv("page2", page2));
  }

  public static void daoQueries(MtDb db, MtJdbc jdbc) {
    var pDao = new PhoneDao(db.schema, fmt, jdbc, m3Ifn);
    var smsField = pDao.dsc.getField(PhoneDao.fld_smsVerificationCode);

    assertEquals("_8o.smsVerificationCode IS NOT NULL", pDao.query().isNotNull(smsField).renderFilter());
    assertEquals("_8o.smsVerificationCode IS NULL", pDao.query().isNull(smsField).renderFilter());
    assertEquals("_8o.smsVerificationCode = :p0", pDao.query().eq(smsField, 123).renderFilter());
    assertEquals("_8o.smsVerificationCode < :p0", pDao.query().lt(smsField, 123).renderFilter());
    assertEquals("_8o.smsVerificationCode <= :p0", pDao.query().lte(smsField, 123).renderFilter());
    assertEquals("_8o.smsVerificationCode > :p0", pDao.query().gt(smsField, 123).renderFilter());
    assertEquals("_8o.smsVerificationCode >= :p0", pDao.query().gte(smsField, 123).renderFilter());

    assertEquals(
      "_8o.smsVerificationCode IS NOT NULL AND _8o.smsVerificationCode = :p0 AND _8o.smsVerificationCode > :p1",
      pDao.query()
        .isNotNull(smsField)
        .and().eq(smsField, 123)
        .and().gt(smsField, 0).renderFilter()
    );
    assertEquals(
      "_8o.smsVerificationCode IS NULL OR _8o.smsVerificationCode = :p0 AND _8o.smsVerificationCode > :p1",
      pDao.query()
        .isNull(smsField)
        .or().eq(smsField, 123)
        .and().gt(smsField, 0).renderFilter()
    );
  }

  public static void daoJoins(MtDb db, MtJdbc jdbc) {
    var apiKeyDao = new ApiKeyDao(db.schema, fmt, jdbc, xxIfn);
    var nsDao = new NamespaceDao(db.schema, fmt, jdbc, xxIfn);
    var knsDao = new KeyNamespaceDao(db.schema, fmt, jdbc, xxIfn);
    var uDao = new DbUserDao(db.schema, fmt, jdbc, m3Ifn);

    var jane = uDao.loadWhereEmailEq(u0.email).iterator().next();
    var apiKey0 = apiKeyDao.upsert(ApiKey.of(jane.uid, null, "test-key", "DEADBEEF"));

    var ns0 = nsDao.upsert(Namespace.of("namespace-00", "/ns00"));
    var ns1 = nsDao.upsert(Namespace.of("namespace-01", "/ns01"));
    var ns2 = nsDao.upsert(Namespace.of("namespace-02", "/ns02"));

    knsDao.upsert(KeyNamespace.of(apiKey0.rec.kid, ns0.rec.nsId));
    knsDao.upsert(KeyNamespace.of(apiKey0.rec.kid, ns1.rec.nsId));
    knsDao.upsert(KeyNamespace.of(apiKey0.rec.kid, ns2.rec.nsId));

    var ps = 2;
    var flName = NamespaceDao.fld_name;
    var flUtcMs = NamespaceDao.fld_createdAtUtcMs;

    var ij = nsDao
      .query()
      .innerJoin(knsDao.dsc, nsDao.dsc)
      .eq(knsDao.fld_kid(), apiKey0.rec.kid)
      .limit(ps);
    var p1 = nsDao.loadPage1(ij, flName, null);
    while (p1.hasNext()) {
      log.info("{}", kv("namespaces", p1.items));
      p1 = nsDao.loadPage1(ij, flName, p1.nx1);
    }
    log.info("{}", kv("namespaces", p1.items));

    var lj = nsDao
      .query()
      .from(nsDao.dsc)
      .leftJoin(knsDao.dsc, nsDao.dsc)
      .eq(knsDao.fld_kid(), apiKey0.rec.kid)
      .limit(ps)
      .reverse();
    var p2 = nsDao.loadPage2(lj, flName, null, flUtcMs, null);
    while (p2.hasNext()) {
      log.info("{}", kv("namespaces", p2.items));
      p2 = nsDao.loadPage2(lj, flName, p2.nx1, flUtcMs, p2.nx2);
    }
    log.info("{}", kv("namespaces", p2.items));
  }

}
