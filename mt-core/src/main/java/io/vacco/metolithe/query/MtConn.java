package io.vacco.metolithe.query;

import java.sql.Connection;
import java.util.function.Supplier;

public interface MtConn extends Supplier<Connection> {
  boolean inTx();
}
