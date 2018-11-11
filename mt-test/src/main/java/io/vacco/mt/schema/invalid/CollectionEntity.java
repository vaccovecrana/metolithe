package io.vacco.mt.schema.invalid;

import io.vacco.metolithe.annotations.MtAttribute;
import io.vacco.metolithe.annotations.MtEntity;
import io.vacco.metolithe.annotations.MtId;
import io.vacco.metolithe.annotations.MtIdGroup;

import java.util.HashSet;
import java.util.Set;

@MtEntity
public class CollectionEntity {
  @MtId public long entId;
  @MtIdGroup(number = 0, position = 1) public String entName;
  @MtAttribute(nil = false, len = 64)
  public Set<String> options = new HashSet<>();
}
