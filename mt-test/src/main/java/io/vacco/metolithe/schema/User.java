package io.vacco.metolithe.schema;

import io.vacco.metolithe.annotations.*;

@MtEntity public class User {
  @MtPk public int uid;
  @St32 public String pw;
  @St64 public String alias;
  @St128 @MtUnique(idx = 0, inPk = false) public String email;
  @MtFk(DeviceTag.class) public long tid;
  @St64Opt public String tagSignature;
}
