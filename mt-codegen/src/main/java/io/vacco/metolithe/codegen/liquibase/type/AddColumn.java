package io.vacco.metolithe.codegen.liquibase.type;

import java.util.ArrayList;
import java.util.List;

public class AddColumn implements MtLbType {
  public String tableName;
  public List<Column> columns = new ArrayList<>();
}
