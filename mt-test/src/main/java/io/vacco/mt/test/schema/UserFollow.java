package io.vacco.mt.test.schema;

import io.vacco.metolithe.annotations.*;

@MtEntity public class UserFollow {
  @MtPk public int fid;
  @MtFk(DbUser.class)
  @MtUnique(idx = 0, inPk = true) public int fromUid;
  @MtFk(DbUser.class)
  @MtUnique(idx = 1, inPk = true) public int toUid;
}
