package io.vacco.metolithe.codegen.liquibase.type;

import java.util.ArrayList;
import java.util.List;

public class Root implements MtLbType {

  public List<ChangeSet> databaseChangeLog = new ArrayList<>();

  public void append(ChangeSet cs) {
    databaseChangeLog.add(cs);
  }

}
