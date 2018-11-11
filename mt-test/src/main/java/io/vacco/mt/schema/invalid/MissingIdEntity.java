package io.vacco.mt.schema.invalid;

import io.vacco.metolithe.annotations.MtAttribute;
import io.vacco.metolithe.annotations.MtEntity;

@MtEntity
public class MissingIdEntity {
  @MtAttribute(nil = false, len = 4) public String entName;
}
