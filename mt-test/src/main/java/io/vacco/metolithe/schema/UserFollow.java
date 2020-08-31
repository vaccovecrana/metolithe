package io.vacco.metolithe.schema;

import io.vacco.metolithe.annotations.*;

@MtEntity public class UserFollow {
  @MtFk(User.class)
  @MtUnique(idx = 0, inPk = false) public int from;
  @MtFk(User.class)
  @MtUnique(idx = 1, inPk = false) public int to;
}
