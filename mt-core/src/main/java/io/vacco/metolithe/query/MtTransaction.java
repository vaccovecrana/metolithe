package io.vacco.metolithe.query;

import io.vacco.metolithe.core.MtLog;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

import static io.vacco.metolithe.core.MtErr.*;

public class MtTransaction implements AutoCloseable, MtConn {

  private MtConn connFn;
  private Connection txConn;
  private boolean isOpen;
  private boolean shouldCommit = true;

  public MtTransaction withSupplier(MtConn connFn) {
    this.connFn = Objects.requireNonNull(connFn);
    return this;
  }

  @Override public Connection get() {
    return txConn;
  }

  public void start(Consumer<Connection> txConnFn) {
    try {
      txConn = connFn.get();
      txConn.setAutoCommit(false);
      isOpen = true;
      txConnFn.accept(txConn);
    } catch (SQLException e) {
      throw generalError("Failed to start transaction", e);
    }
  }

  public void rollback() {
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
        txConn.close();
      } catch (SQLException e) {
        MtLog.warn("Failed to close connection", e);
      }
    }
  }

}