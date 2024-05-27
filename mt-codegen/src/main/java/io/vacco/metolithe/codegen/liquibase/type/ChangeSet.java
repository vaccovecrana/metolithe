package io.vacco.metolithe.codegen.liquibase.type;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChangeSet implements MtLbType {

  public String id, author = "generated";
  public List<MtLbType> changes = new ArrayList<>();

  public ChangeSet withId(String id) {
    this.id = id;
    return this;
  }

  public ChangeSet add(MtLbType t) {
    changes.add(t);
    return this;
  }

  @Override public String toString() {
    return String.format("%s %s",
      changes.stream()
        .map(MtLbType::getLabelName)
        .collect(Collectors.toList()),
      id
    );
  }
}
