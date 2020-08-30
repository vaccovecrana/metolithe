package io.vacco.metolithe.schema;

import io.vacco.metolithe.annotations.*;

@MtEntity public class Device {

  public enum DType { ANDROID, IOS, OTHER }

  // a totally random number, possibly salted with a device's app advertising ID.
  @MtPk public long did;
  @MtField @MtNotNull DType type;
  @St512 public String signingKey;
}
