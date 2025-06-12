package io.vacco.metolithe.query;

import java.sql.Connection;
import java.util.Objects;

public class MtResult<T> {

  public T     rec;
  public MtCmd cmd;

  public MtResult<T> on(Connection conn) {
    this.cmd.executeOn(conn);
    return this;
  }

  public static <T> MtResult<T> result(T rec, MtCmd cmd) {
    var r = new MtResult<T>();
    r.rec = rec;
    r.cmd = Objects.requireNonNull(cmd);
    return r;
  }

}
