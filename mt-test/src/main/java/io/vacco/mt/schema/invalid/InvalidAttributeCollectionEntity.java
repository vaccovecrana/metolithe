package io.vacco.mt.schema.invalid;

import io.vacco.metolithe.annotations.*;
import java.util.HashSet;
import java.util.Set;

@MtEntity
public class InvalidAttributeCollectionEntity {
  @MtId public long entId;
  @MtIdGroup(number = 0, position = 1)
  @MtAttribute(nil = false, len = 32) public String entName;
  @MtAttribute(nil = false, len = 32)
  @MtCollection(sqlType = "varchar(128)") public Set<String> options = new HashSet<>();
}
