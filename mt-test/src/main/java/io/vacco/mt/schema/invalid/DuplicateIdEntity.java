package io.vacco.mt.schema.invalid;

import io.vacco.metolithe.annotations.MtEntity;
import io.vacco.metolithe.annotations.MtId;

@MtEntity
public class DuplicateIdEntity {
  @MtId public long entId;
  @MtId public String entName;
}
