package io.vacco.metolithe.changeset;

public class MtSql {

  public String script;

  public MtSql withLines(String ... lines) {
    this.script = String.join("\n", lines);
    return this;
  }

}
