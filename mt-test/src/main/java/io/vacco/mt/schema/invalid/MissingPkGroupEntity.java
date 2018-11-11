package io.vacco.mt.schema.invalid;

import io.vacco.metolithe.annotations.MtAttribute;
import io.vacco.metolithe.annotations.MtId;

public class MissingPkGroupEntity {
  @MtId public String entId;
  @MtAttribute(nil = false, len = 4) public String entName;
}
