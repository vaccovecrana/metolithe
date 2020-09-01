package io.vacco.metolithe.test;

import io.vacco.metolithe.schema.*;
import io.vacco.shax.logging.ShOption;

public abstract class MtSpec {
  static {
    ShOption.setSysProp(ShOption.IO_VACCO_SHAX_DEVMODE, "true");
    ShOption.setSysProp(ShOption.IO_VACCO_SHAX_PRETTYPRINT, "true");
  }

  public static Class<?>[] testSchema = new Class<?>[] {
      Device.class, DeviceLocation.class, DeviceTag.class,
      Phone.class, User.class, UserFollow.class
  };
}
