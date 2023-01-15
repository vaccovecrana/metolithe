package io.vacco.metolithe.codegen.liquibase.type;

public class AddForeignKeyConstraint implements MtLbType {
  public String
    baseColumnNames, baseTableName, constraintName,
    referencedColumnNames, referencedTableName;
}
