package io.vacco.mt.test.schema;

import io.vacco.metolithe.annotations.*;
import io.vacco.mt.test.annotations.St512;

@MtEntity public class Device {

  public enum DType { ANDROID, IOS, OTHER }

  @MtPk public long did;

  @MtField @MtNotNull
  public DType type;

  @St512 @MtPk(idx = 0)
  public String signingKey;

}
