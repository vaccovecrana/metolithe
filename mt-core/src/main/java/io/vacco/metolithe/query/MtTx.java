package io.vacco.metolithe.query;

import io.vacco.metolithe.core.MtLog;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

import static io.vacco.metolithe.core.MtErr.*;

public class MtTx implements AutoCloseable, MtConn {

  private MtConn      connFn;
  private Connection  txConn;
  private boolean     isOpen;
  private boolean     shouldCommit = true;

  public List<SQLWarning> warnings = new ArrayList<>();
  public Exception        error;

  public MtTx withSupplier(MtConn connFn) {
    this.connFn = Objects.requireNonNull(connFn);
    return this;
  }

  public void run(Consumer<Connection> txConnFn) {
    try {
      this.txConn = connFn.get();
      this.txConn.setAutoCommit(false);
      this.isOpen = true;
      txConnFn.accept(txConn);
    } catch (SQLException e) {
      throw generalError("Failed to run transaction", e);
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
        var txw = txConn.getWarnings();
        while (txw != null) {
          this.warnings.add(txw);
          txw = txw.getNextWarning();
        }
        txConn.clearWarnings();
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