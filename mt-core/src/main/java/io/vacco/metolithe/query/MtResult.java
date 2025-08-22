package io.vacco.metolithe.query;

import java.util.Objects;

public class MtResult<T> {

  public T     rec;
  public MtCmd cmd;

  public static <T> MtResult<T> result(T rec, MtCmd cmd) {
    var r = new MtResult<T>();
    r.rec = rec;
    r.cmd = Objects.requireNonNull(cmd);
    return r;
  }

}
