package io.vacco.metolithe.schema;

import io.vacco.metolithe.annotations.*;

@MtEntity public class UserFollow {
  @MtFk(DbUser.class)
  @MtUnique(idx = 0, inPk = false) public int fromUid;
  @MtFk(DbUser.class)
  @MtUnique(idx = 1, inPk = false) public int toUid;
}
