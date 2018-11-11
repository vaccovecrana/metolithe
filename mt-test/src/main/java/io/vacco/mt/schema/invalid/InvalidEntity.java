package io.vacco.mt.schema.invalid;

import io.vacco.metolithe.annotations.MtAttribute;
import io.vacco.metolithe.annotations.MtEntity;
import io.vacco.metolithe.annotations.MtId;
import io.vacco.metolithe.annotations.MtIdGroup;

@MtEntity
public class InvalidEntity {
  @MtId public String entId;
  @MtIdGroup(number = 0, position = 1)
  @MtAttribute(nil = false, len = 4)
  public String entName;
}
