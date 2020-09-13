package io.vacco.metolithe.schema;

import io.vacco.metolithe.annotations.*;

@MtEntity public class Device {
  public enum DType { ANDROID, IOS, OTHER }
  @MtPk public long did;
  @MtField @MtNotNull public DType type;
  @St512 @MtUnique(idx = 0, inPk = true) public String signingKey;
}
