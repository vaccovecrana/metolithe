package io.vacco.mt.test.schema;

import io.vacco.metolithe.annotations.MtPk;
import io.vacco.metolithe.annotations.MtVarchar;

public class TransientSchema {
  @MtPk
  @MtVarchar(255)
  public String id;

  @MtVarchar(255)
  public String name;

  public transient String secret;
}
