package io.vacco.mt.schema.valid;

import io.vacco.metolithe.annotations.*;
import java.util.HashSet;
import java.util.Set;

@MtEntity
public class CollectionEntity {
  @MtId public long entId;
  @MtIdGroup(number = 0, position = 1)
  @MtAttribute(nil = false, len = 32) public String entName;
  @MtCollection public Set<String> options = new HashSet<>();
}
