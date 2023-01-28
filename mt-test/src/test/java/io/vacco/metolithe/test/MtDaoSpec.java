package io.vacco.metolithe.test;

import io.vacco.metolithe.codegen.dao.MtDaoMapper;
import io.vacco.metolithe.codegen.liquibase.*;
import io.vacco.metolithe.core.*;
import io.vacco.metolithe.schema.*;
import io.vacco.metolithe.test.dao.*;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import liquibase.*;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.codejargon.fluentjdbc.api.*;
import org.junit.runner.RunWith;

import static j8spec.J8Spec.*;
import static org.junit.Assert.*;
import static io.vacco.shax.logging.ShArgument.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class MtDaoSpec extends MtSpec {

  private static final String schema = "public";
  private static final FluentJdbc jdbc = new FluentJdbcBuilder()
      .connectionProvider(ds)
      .afterQueryListener(edt -> log.info("[{}], {}", edt.success(), edt.sql()))
      .build();

  private static final MtIdFn<Integer> m3Ifn = new MtMurmur3IFn();

  static {
    describe("Query parameter building", () -> {
      it("Renders queries with placeholder field names", () -> {
        var q = new MtQuery()
            .as("select * from gopher.blog_entry where $0 = :$0 and $1 >= :$1 order by $1 limit 8")
            .withSlotValue("category")
            .withSlotValue("publishedUtcMs");
        log.info("rendered: [{}]", q.render());
      });
    });

    var xmlFile = new File("./build", "mt-test.xml");
    var ymlFile = new File("./build", "mt-test.yml");
    ds.setURL("jdbc:h2:mem:public;DB_CLOSE_DELAY=-1");

    describe("Schema code generation", () -> {
      it("Generates typed field DAO definitions", () -> {
        var out = new File(".", "src/main/java");
        new MtDaoMapper().mapSchema(out, "io.vacco.metolithe.test.dao", fmt, Phone.class, DbUser.class);
      });
      it("Generates Liquibase changelogs", () -> {
        var root = new MtLb().build(fmt, testSchema);
        var xmlGen = new MtLbXml();
        var ymlGen = new MtLbYaml();

        var xmlBaos = new ByteArrayOutputStream();
        xmlGen.writeSchema(root, xmlBaos);
        log.info(xmlBaos.toString());

        var ymlBaos = new ByteArrayOutputStream();
        ymlGen.writeSchema(root, new OutputStreamWriter(ymlBaos));
        log.info(ymlBaos.toString());

        xmlGen.writeSchema(root, new FileOutputStream(xmlFile));
        ymlGen.writeSchema(root, new OutputStreamWriter(new FileOutputStream(ymlFile)));
      });
      it("Creates an in-memory database and applies the generated change logs.", () -> {
        var c = new JdbcConnection(ds.getConnection());
        var ra = new DirectoryResourceAccessor(xmlFile.getParentFile());
        var lb = new Liquibase(ymlFile.getName(), ra, c);
        lb.update(new Contexts(), new LabelExpression());
        assertNotNull(c);
        c.close();
      });
    });

    var pDao = new PhoneDao(schema, fmt, jdbc, m3Ifn);

    describe("Type safe DAOs", () -> {
      it("Creates base DAOs for data access", () -> {
        var dDao = new MtWriteDao<>(schema, jdbc, new MtDescriptor<>(Device.class, fmt), new MtMurmur3LFn());
        var uDao = new MtWriteDao<>(schema, jdbc, new MtDescriptor<>(DbUser.class, fmt), m3Ifn);
        var dtDao = new MtWriteDao<>(schema, jdbc, new MtDescriptor<>(DeviceTag.class, fmt), new MtMurmur3LFn());
        var ufDao = new MtWriteDao<>(schema, jdbc, new MtDescriptor<>(UserFollow.class, fmt), new MtMurmur3IFn());

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
        log.info("{}", kv("p0m", pDao.merge(p0)));

        log.info("{}", kv("d0m", dDao.merge(d0)));
        d0.type = Device.DType.IOS;
        log.info("{}", kv("d0u", dDao.merge(d0)));
        log.info("{}", kv("d1s", dDao.merge(d1)));

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

        log.info("{}", kv("dt0Id", dtDao.idOf(dt0).get()));
        log.info("{}", kv("dt0m", dtDao.merge(dt0)));
        log.info("{}", kv("dt1m", dtDao.merge(dt1)));

        u0.tid = dt0.tid;
        u1.tid = dt1.tid;

        log.info("{}", kv("u0Id", uDao.idOf(u0).get()));
        log.info("{}", kv("u0m", uDao.merge(u0)));
        log.info("{}", kv("u1m", uDao.merge(u1)));

        var uf0 = new UserFollow();
        uf0.fromUid = u0.uid;
        uf0.toUid = u1.uid;

        log.info("{}", kv("uf0m", ufDao.merge(uf0)));
      });

      it("Uses generated POJO DAOs for data access", () -> {
        var ud = new DbUserDao(schema, fmt, jdbc, new MtMurmur3IFn());
        log.info("{}", kv("loadWhereEqJane", ud.loadWhereAliasEq("Jane")));
        log.info("{}", kv("loadWhereEmailEq", ud.loadWhereEmailIn("joe@me.com")));
        log.info("");
      });

      it("Can paginate over record collections", () -> {
        var r = new Random();
        var phones = IntStream.range(0, 64).mapToObj(i -> {
          var p = new Phone();
          p.countryCode = r.nextBoolean() ? 1 : r.nextBoolean() ? 2 : 3;
          p.number = RandomStringUtils.randomNumeric(10);
          p.smsVerificationCode = r.nextBoolean() ? Integer.parseInt(RandomStringUtils.randomNumeric(6)) : 0;
          return p;
        }).collect(Collectors.toList());
        for (var p : phones) { pDao.merge(p); } // TODO implement/change to batch support.

        log.info("======== All phone pages ========");
        phones.clear();
        var page0 = pDao.loadPage1(16, null, PhoneDao.fld_number, null);
        phones.addAll(page0.items);
        while (page0.nx1 != null) {
          log.info("{}", kv("page0", page0));
          page0 = pDao.loadPage1(16, null, PhoneDao.fld_number, page0.nx1);
          phones.addAll(page0.items);
        }
        log.info("{}", kv("page0", page0));

        log.info("======== Verified phone pages ========");
        var pageSum = 0L;
        var fq = MtQuery.of("$0 != :$0")
            .withSlotValue(PhoneDao.fld_smsVerificationCode)
            .withParam(PhoneDao.fld_smsVerificationCode, 0);
        var page1 = pDao.loadPage1(4, fq, PhoneDao.fld_number, null);
        pageSum = pageSum + page1.size;
        while (page1.nx1 != null) {
          log.info("{}", kv("page1", page1));
          page1 = pDao.loadPage1(4, fq, PhoneDao.fld_number, page1.nx1);
          pageSum = pageSum + page1.size;
        }
        log.info("{}", kv("page1", page1));

        var vp = phones.stream().filter(p -> p.smsVerificationCode != 0).collect(Collectors.toList());
        var uvp = phones.stream().filter(p -> p.smsVerificationCode == 0).collect(Collectors.toList());

        assertEquals(pageSum, vp.size());

        log.info("====> All phones: {}", phones.size());
        log.info("====> Verified phones: {}", vp.size());
        log.info("====> Unverified phones: {}", uvp.size());

        log.info("======== Verified, country code sorted phone pages ========");
        var page2 = pDao.loadPage2(
          4, fq,
          PhoneDao.fld_countryCode, null,
          PhoneDao.fld_number, null
        );
        while (page2.nx1 != null) {
          log.info("{}", kv("page2", page2));
          page2 = pDao.loadPage2(
            4, fq,
            PhoneDao.fld_countryCode, page2.nx1,
            PhoneDao.fld_number, page2.nx2
          );
        }
        log.info("{}", kv("page2", page2));
      });
    });

  }
}
