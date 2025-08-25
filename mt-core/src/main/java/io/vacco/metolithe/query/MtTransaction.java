package io.vacco.metolithe.query;

import io.vacco.metolithe.core.MtLog;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

import static io.vacco.metolithe.core.MtErr.*;

public class MtTransaction implements AutoCloseable, MtConn {

  private MtConn               connFn;
  private Connection           txConn;
  private Consumer<Connection> afterTxFn;

  private boolean isOpen;
  private boolean shouldCommit = true;

  public MtTransaction withSupplier(MtConn connFn) {
    this.connFn = Objects.requireNonNull(connFn);
    return this;
  }

  public void start(Consumer<Connection> txConnFn, Consumer<Connection> afterTxFn) {
    try {
      this.txConn = connFn.get();
      this.txConn.setAutoCommit(false);
      this.isOpen = true;
      this.afterTxFn = afterTxFn;
      txConnFn.accept(txConn);
    } catch (SQLException e) {
      throw generalError("Failed to start transaction", e);
    }
  }

  @Override public void rollback() {
    shouldCommit = false;
  }

  @Override public void close() {
    try {
      if (isOpen) {
        if (shouldCommit) {
          try {
            txConn.commit();
          } catch (SQLException e) {
            throw generalError("Commit failed", e);
          }
        } else {
          try {
            txConn.rollback();
          } catch (SQLException e) {
            throw generalError("Rollback failed", e);
          }
        }
        isOpen = false;
      }
    } finally {
      try {
        txConn.setAutoCommit(true);
        if (this.afterTxFn != null) {
          this.afterTxFn.accept(txConn);
        }
        txConn.close();
      } catch (SQLException e) {
        MtLog.warn("Failed to close connection", e);
      }
    }
  }

  @Override public Connection get() {
    return txConn;
  }

  @Override public boolean inTx() {
    return true;
  }

}