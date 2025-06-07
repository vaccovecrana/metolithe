package io.vacco.mt.test.schema;

import io.vacco.metolithe.annotations.*;
import io.vacco.mt.test.annotations.*;

@MtEntity public class DbUser {
  @MtPk public int uid;
  @St32
  public String pw;
  @St64
  public String alias;
  @St128
  @MtUnique(idx = 0, inPk = false) public String email;
  @MtFk(DeviceTag.class) public long tid;
  @St64Opt
  public String tagSignature;
  @MtFk(DbUserRole.class) @St16
  public String rid;
}
