package io.vacco.metolithe.test;

import io.vacco.metolithe.schema.*;
import io.vacco.shax.logging.ShOption;

public abstract class MtSpec {

  public static User u0 = new User();
  public static Phone p0 = new Phone();
  public static Device d0 = new Device();

  static {
    ShOption.setSysProp(ShOption.IO_VACCO_SHAX_DEVMODE, "true");
    ShOption.setSysProp(ShOption.IO_VACCO_SHAX_PRETTYPRINT, "true");

    u0.alias = "Jane";
    u0.email = "jane@me.com";
    u0.pw = "0xAAABBBCCCDDDEEFFF";
    u0.uid = 112233;

    p0.countryCode = 1;
    p0.number = "5555555555";
    p0.smsVerificationCode = 1234;

    d0.type = Device.DType.ANDROID;
    d0.signingKey = "-----BEGIN PUBLIC KEY-----\n" +
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDvjvvabCydtvNmDPrHNCQypr1/\n" +
        "BjT54hwLXbuA8rnDmZOO5o/M5+FgODHpSS/h1IsvC7SxKkHHgYwTE4SuJb7umqwx\n" +
        "M8Yvnv4jRyTwM71/kDaUBs+xHoKBk2A1qn8oOW8Ub/7wP2/o6YHtuerAkEIM5jBN\n" +
        "Et1+/x6vjLwCEix44QIDAQAB\n" +
        "-----END PUBLIC KEY-----\n";
  }

  public static Class<?>[] testSchema = new Class<?>[] {
      Device.class, DeviceLocation.class, DeviceTag.class,
      Phone.class, User.class, UserFollow.class
  };

}
