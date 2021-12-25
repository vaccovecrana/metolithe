package io.vacco.metolithe.test;

import io.vacco.metolithe.core.MtCaseFormat;
import io.vacco.metolithe.schema.*;
import io.vacco.shax.logging.ShOption;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.*;

public abstract class MtSpec {

  static {
    ShOption.setSysProp(ShOption.IO_VACCO_SHAX_DEVMODE, "true");
    ShOption.setSysProp(ShOption.IO_VACCO_SHAX_PRETTYPRINT, "true");
  }

  protected static final Logger log = LoggerFactory.getLogger(MtAnnotationsSpec.class);

  protected static final JdbcDataSource ds = new JdbcDataSource();
  protected static final MtCaseFormat fmt = MtCaseFormat.KEEP_CASE;

  public static User u0 = new User();
  public static User u1 = new User();

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
  }

  public static Class<?>[] testSchema = new Class<?>[] {
      Device.class, DeviceLocation.class, DeviceTag.class,
      Phone.class, User.class, UserFollow.class
  };
}
