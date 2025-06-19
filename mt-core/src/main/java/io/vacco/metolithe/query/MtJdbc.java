package io.vacco.metolithe.query;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.function.*;

import static io.vacco.metolithe.core.MtLog.debug;
import static io.vacco.metolithe.core.MtErr.*;

public class MtJdbc implements MtConn {

  private MtConn connFn;

  public MtCmd select(String sql) {
    return new MtCmd(sql);
  }

  public MtCmd update(String sql) {
    return new MtCmd(sql);
  }

  public void transaction(BiConsumer<MtConn, Connection> txFn) {
    try (var tx = new MtTransaction().withSupplier(this)) {
      tx.start(conn -> txFn.accept(tx, conn));
    } catch (Exception e) {
      throw generalError("Transaction failed", e);
    }
  }

  public void batch(MtConn connFn, Consumer<List<MtResult<?>>> batchFn) throws SQLException {
    var results = new ArrayList<MtResult<?>>();
    batchFn.accept(results);
    var idx = new LinkedHashMap<String, List<MtResult<?>>>();
    for (var res : results) {
      idx.computeIfAbsent(
        res.cmd.prepareSql().sqlP, k -> new ArrayList<>()
      ).add(res);
    }
    for (var e : idx.entrySet()) {
      var sql = e.getValue().get(0).cmd.sqlP;
      debug("Executing batch [{}]", sql);
      try (var ps = connFn.get().prepareStatement(sql)) {
        for (var res : e.getValue()) {
          res.cmd.fill(ps);
          ps.addBatch();
        }
        var counts = ps.executeBatch();
        for (int i = 0; i < counts.length; i++) {
          e.getValue().get(i).cmd.rowCount = counts[i];
        }
      }
    }
  }

  public void batch(Consumer<List<MtResult<?>>> batchFn) throws SQLException {
    batch(this, batchFn);
  }

  public MtJdbc withSupplier(MtConn connFn) {
    this.connFn = Objects.requireNonNull(connFn);
    return this;
  }

  public MtJdbc withSupplier(DataSource ds) {
    return withSupplier(() -> {
      try {
        return ds.getConnection();
      } catch (SQLException e) {
        throw generalError("Error getting database connection", e);
      }
    });
  }

  @Override public Connection get() {
    return connFn.get();
  }

}
