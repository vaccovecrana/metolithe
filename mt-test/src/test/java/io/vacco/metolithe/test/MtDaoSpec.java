package io.vacco.metolithe.test;

import io.vacco.metolithe.core.*;
import io.vacco.metolithe.schema.*;
import io.vacco.metolithe.test.dao.PhoneDao;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.apache.commons.lang3.RandomStringUtils;
import org.codejargon.fluentjdbc.api.*;
import org.junit.runner.RunWith;

import static j8spec.J8Spec.*;
import static org.junit.Assert.*;
import static io.vacco.shax.logging.ShArgument.*;

import io.vacco.metolithe.test.dao.UserDao;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class MtDaoSpec extends MtSpec {

  private static final String schema = "public";
  private static final FluentJdbc jdbc = new FluentJdbcBuilder()
      .connectionProvider(ds)
      .afterQueryListener(edt -> log.info("[{}], {}", edt.success(), edt.sql()))
      .build();

  private static final MtIdFn<Integer> m3Ifn = new MtMurmur3IFn();
  private static final PhoneDao pDao = new PhoneDao(schema, fmt, jdbc, m3Ifn);

  static {
    it("Creates base DAOs for data access", () -> {

      MtWriteDao<Device, Long> dDao = new MtWriteDao<>(
          schema, jdbc, new MtDescriptor<>(Device.class, fmt), new MtMurmur3LFn());
      MtWriteDao<User, Integer> uDao = new MtWriteDao<>(
          schema, jdbc, new MtDescriptor<>(User.class, fmt), m3Ifn);
      MtWriteDao<DeviceTag, Long> dtDao = new MtWriteDao<>(
          schema, jdbc, new MtDescriptor<>(DeviceTag.class, fmt), new MtMurmur3LFn());
      MtWriteDao<UserFollow, Void> ufDao = new MtWriteDao<>(
          schema, jdbc, new MtDescriptor<>(UserFollow.class, fmt), new MtNoopIdFn());

      log.info("{}", kv("p0", pDao.save(p0)));
      Phone p01 = pDao.loadExisting(p0.pid);
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

      DeviceTag dt0 = new DeviceTag();
      dt0.claimTimeUtcMs = System.currentTimeMillis();
      dt0.did = d0.did;
      dt0.pid = p0.pid;
      dt0.smsCodeSignature = "U29tZSBzaWduYXR1cmUgZm9yIHRoZSBudW1iZXIgMTIzNA==";

      DeviceTag dt1 = new DeviceTag();
      dt1.claimTimeUtcMs = System.currentTimeMillis();
      dt1.did = d1.did;
      dt1.pid = p1.pid;
      dt1.smsCodeSignature = "U29tZSBzaWduYXR1cmUgZm9yIHRoZSBudW1iZXIgNDU2Nw==";

      log.info("{}", kv("dt0m", dtDao.merge(dt0)));
      log.info("{}", kv("dt1m", dtDao.merge(dt1)));

      u0.tid = dt0.tid;
      u1.tid = dt1.tid;

      log.info("{}", kv("u0m", uDao.merge(u0)));
      log.info("{}", kv("u1m", uDao.merge(u1)));

      UserFollow uf0 = new UserFollow();
      uf0.fromUid = u0.uid;
      uf0.toUid = u1.uid;

      log.info("{}", kv("uf0m", ufDao.merge(uf0)));
    });

    it("Uses generated POJO DAOs for data access", () -> {
      UserDao ud = new UserDao(schema, fmt, jdbc, new MtMurmur3IFn());
      log.info("{}", kv("loadWhereEqJane", ud.loadWhereAliasEq("Jane")));
      log.info("{}", kv("loadWhereEmailEq", ud.loadWhereEmailIn("joe@me.com")));
      log.info("");
    });

    it("Can paginate over record collections", () -> {
      List<Phone> phones = IntStream.range(0, 64).mapToObj(i -> {
        Phone p = new Phone();
        p.countryCode = 1;
        p.number = RandomStringUtils.randomNumeric(10);
        p.smsVerificationCode = Integer.parseInt(RandomStringUtils.randomNumeric(6));
        return p;
      }).collect(Collectors.toList());
      for (Phone p : phones) { pDao.merge(p); } // TODO implement/change to batch support.
      MtKeysetPage<Phone, String> page0 = pDao.loadPage(null, PhoneDao.fld_number, 16);
      while (page0.next != null) {
        log.info("{}", kv("page0", page0));
        page0 = pDao.loadPage(page0.next, PhoneDao.fld_number, 16);
      }
      log.info("{}", kv("page0", page0));
    });
  }
}
