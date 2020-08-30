package io.vacco.metolithe.schema;

import io.vacco.metolithe.annotations.*;

@MtEntity public class DeviceTag {

  @MtPk public long tid;

  @MtFk(Phone.class)
  @MtUnique(position = 0)
  public int pid;

  @MtField public long claimTimeUtcMs;
  @St64 public String smsCodeSignature;
}
