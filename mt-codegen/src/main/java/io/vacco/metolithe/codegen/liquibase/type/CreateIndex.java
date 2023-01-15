package io.vacco.metolithe.codegen.liquibase.type;

import java.util.ArrayList;
import java.util.List;

public class CreateIndex implements MtLbType {

  public String indexName, tableName;
  public List<Column> columns = new ArrayList<>();

  public CreateIndex withIndexName(String indexName) {
    this.indexName = indexName;
    return this;
  }

  public CreateIndex withTableName(String tableName) {
    this.tableName = tableName;
    return this;
  }
}
