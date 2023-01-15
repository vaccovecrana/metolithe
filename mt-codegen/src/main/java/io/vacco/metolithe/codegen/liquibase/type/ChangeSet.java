package io.vacco.metolithe.codegen.liquibase.type;

import java.util.ArrayList;
import java.util.List;

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
}
