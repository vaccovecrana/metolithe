package io.vacco.mt.schema.invalid;

import io.vacco.metolithe.annotations.*;

@MtEntity
public class MismatchingIdEntity {
  @MtId public String id = "";
  @MtIdGroup(number = 0, position = 0)
  @MtAttribute(nil = false, len = 4)
  public String entName;
}
