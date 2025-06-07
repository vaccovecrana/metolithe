package io.vacco.mt.test.schema;

import io.vacco.metolithe.annotations.*;
import io.vacco.mt.test.annotations.St128;

import java.util.Objects;

@MtEntity public class Namespace {

  @MtPk public Integer nsId;

  @St128
  public String name;

  @St128
  @MtUnique(idx = 0, inPk = true)
  public String path;

  @MtField
  public long createdAtUtcMs;

  public static Namespace of(String name, String path) {
    var ns = new Namespace();
    ns.name = Objects.requireNonNull(name);
    ns.path = Objects.requireNonNull(path);
    ns.createdAtUtcMs = System.currentTimeMillis();
    return ns;
  }

  @Override public String toString() {
    return String.format("%d, %s, %s", nsId, name, path);
  }

}
