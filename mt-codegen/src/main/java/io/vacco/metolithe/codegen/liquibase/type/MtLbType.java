package io.vacco.metolithe.codegen.liquibase.type;

public interface MtLbType {
  default String getLabelName() {
    var cn = this.getClass().getSimpleName();
    var c0 = String.valueOf(cn.charAt(0)).toLowerCase();
    return String.format("%s%s", c0, cn.substring(1));
  }
}
