package io.vacco.mt.schema.valid;

import io.vacco.metolithe.annotations.*;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@MtEntity public class CollectionEntity {

  public enum Color { RED, GREEN, BLUE }

  @MtId public long entId;
  @MtIdGroup(number = 0, position = 1)
  @MtAttribute(nil = false, len = 32) public String entName;
  @MtCollection(sqlType = "varchar(4096)") public Set<String> options = new HashSet<>();
  @MtCollection(sqlType = "varchar(4096)") public Set<Color> colors = new TreeSet<>();
}
