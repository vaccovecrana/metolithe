package io.vacco.mt.test.schema;

import io.vacco.metolithe.annotations.*;

@MtEntity public class UserFollow {

  @MtPk public int fid;

  @MtFk(DbUser.class)
  @MtPk(idx = 0) @MtUnique(idx = 0)
  public int fromUid;

  @MtFk(DbUser.class)
  @MtPk(idx = 1) @MtUnique(idx = 0)
  public int toUid;

}
