package io.vacco.mt.test;

import io.vacco.metolithe.core.MtCaseFormat;
import io.vacco.metolithe.id.MtXxHash;
import io.vacco.mt.test.schema.*;
import io.vacco.shax.logging.ShOption;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.*;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public abstract class MtSpec {

  static {
    ShOption.setSysProp(ShOption.IO_VACCO_SHAX_DEVMODE, "true");
    ShOption.setSysProp(ShOption.IO_VACCO_SHAX_PRETTYPRINT, "true");
  }

  protected static final Logger log = LoggerFactory.getLogger(MtSpec.class);

  protected static final JdbcDataSource ds = new JdbcDataSource();
  protected static final MtCaseFormat fmt = MtCaseFormat.KEEP_CASE;

  public static Class<?>[] testSchema = new Class<?>[] {
    Device.class, DeviceLocation.class, DeviceTag.class,
    Phone.class, DbUser.class, DbUserRole.class, UserFollow.class,
    ApiKey.class, Namespace.class, KeyNamespace.class
  };

  public static DbUser u0 = new DbUser();
  public static DbUser u1 = new DbUser();

  public static Phone p0 = new Phone();
  public static Phone p1 = new Phone();

  public static Device d0 = new Device();
  public static Device d1 = new Device();

  static {
    u0.alias = "Jane";
    u0.email = "jane@me.com";
    u0.pw = "0xAAABBBCCCDDDEEFFF";
    u0.uid = 112233;

    u1.alias = "Joe";
    u1.email = "joe@me.com";
    u1.pw = "0xEEEFFFDDDAAABBB";
    u1.uid = 224466;

    p0.countryCode = 1;
    p0.number = "5555555555";
    p0.smsVerificationCode = 1234;

    p1.countryCode = 1;
    p1.number = "4441116666";
    p1.smsVerificationCode = 4567;

    d0.type = Device.DType.ANDROID;
    d0.signingKey = "c29tZSBwdWJsaWMga2V5IGRhdGEgZm9yIGRldmljZSAw";

    d1.type = Device.DType.ANDROID;
    d1.signingKey = "c29tZSBwdWJsaWMga2V5IGRhdGEgZm9yIGRldmljZSAx";

    var d0b = d0.signingKey.getBytes(StandardCharsets.UTF_8);
    var h32 = MtXxHash.hash32(d0b, 0, d0b.length, 1234);
    var h64 = MtXxHash.hash64(d0b, 0, d0b.length, 1234);

    log.info("{}", h32);
    log.info("{}", h64);
    assertEquals(-2013497302, h32);
    assertEquals(-3973416690927799706L, h64);
  }

}
