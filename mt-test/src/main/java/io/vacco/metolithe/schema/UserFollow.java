package io.vacco.metolithe.schema;

import io.vacco.metolithe.annotations.*;

@MtEntity public class UserFollow {
  @MtFk(User.class)
  @MtPk(position = 0)
  public int from;

  @MtPk(position = 1)
  @MtFk(User.class)
  public int to;
}
