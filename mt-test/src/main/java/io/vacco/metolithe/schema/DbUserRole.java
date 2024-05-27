package io.vacco.metolithe.schema;

import io.vacco.metolithe.annotations.*;

@MtEntity
public class DbUserRole {
  @MtPk @St16 public String rid;
  @MtField public long createdUtcMs;
}
