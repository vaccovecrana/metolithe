package io.vacco.metolithe.codegen.liquibase.type;

public class AddUniqueConstraint implements MtLbType {
  public String tableName, constraintName, columnNames;
}
