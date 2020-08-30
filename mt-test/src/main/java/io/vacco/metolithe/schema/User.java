package io.vacco.metolithe.schema;

import io.vacco.metolithe.annotations.*;

@MtEntity public class User {
  @MtPk public int uid;
  @St32 public String pw;
  @St64 public String alias;
  @St128 @MtUnique(position = 0) public String email;

  @MtFk(DeviceTag.class) public long tid;
  @St64 public String tagSignature;
}
