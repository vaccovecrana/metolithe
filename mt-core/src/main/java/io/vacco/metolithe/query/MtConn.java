package io.vacco.metolithe.query;

import java.sql.Connection;
import java.util.function.Supplier;

public interface MtConn extends Supplier<Connection> {

  /**
   * @return <code>true</code> if a transaction is currently active.
   */
  boolean inTx();

  /**
   * Rollback the current transaction, if any.
   */
  void rollback();

}
