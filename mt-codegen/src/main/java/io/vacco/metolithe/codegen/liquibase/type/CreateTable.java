package io.vacco.metolithe.codegen.liquibase.type;

import java.util.ArrayList;
import java.util.List;

public class CreateTable implements MtLbType {

  public String tableName;
  public List<Column> columns = new ArrayList<>();

  public CreateTable withTableName(String tableName) {
    this.tableName = tableName;
    return this;
  }
}
