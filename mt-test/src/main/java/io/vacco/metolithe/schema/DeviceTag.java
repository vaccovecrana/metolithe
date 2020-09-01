package io.vacco.metolithe.schema;

import io.vacco.metolithe.annotations.*;

@MtEntity public class DeviceTag {
  @MtPk public long tid;
  @MtFk(Phone.class) @MtUnique(idx = 0, inPk = true) public int pid;
  @MtFk(Device.class) @MtUnique(idx = 1, inPk = true) public long did;
  @MtField public long claimTimeUtcMs;
  @St64 @MtUnique(idx = 1, inPk = true) public String smsCodeSignature;
}
