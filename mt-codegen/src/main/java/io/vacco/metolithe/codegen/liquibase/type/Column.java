package io.vacco.metolithe.codegen.liquibase.type;

public class Column implements MtLbType {

  public String name, type;
  public Constraints constraints;

  public Column withName(String name) {
    this.name = name;
    return this;
  }

  public Column withType(String type) {
    this.type = type;
    return this;
  }

}
