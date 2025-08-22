package io.vacco.metolithe.query;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.function.*;

import static io.vacco.metolithe.core.MtLog.debug;
import static io.vacco.metolithe.core.MtErr.*;

public class MtJdbc implements MtConn {

  private final DataSource ds;
  private MtConn txFn;

  public MtJdbc(DataSource ds) {
    this.ds = Objects.requireNonNull(ds);
  }

  public MtCmd select(String sql) {
    return new MtCmd(sql, this);
  }

  public MtCmd update(String sql) {
    return new MtCmd(sql, this);
  }

  public void tx(BiConsumer<MtConn, Connection> txFn) {
    try (var tx = new MtTransaction().withSupplier(this)) {
      tx.start(conn -> txFn.accept(tx, conn));
    } catch (Exception e) {
      throw generalError("Transaction failed", e);
    }
  }

  public void txJoin(MtConn txFn, Runnable block) {
    try {
      this.txFn = Objects.requireNonNull(txFn);
      block.run();
    } catch (Exception e) {
      throw generalError("Transaction join failed", e);
    } finally {
      this.txFn = null;
    }
  }

  public List<MtResult<?>> batch(Consumer<List<MtResult<?>>> batchFn) throws SQLException {
    var results = new ArrayList<MtResult<?>>();
    batchFn.accept(results);
    var idx = new LinkedHashMap<String, List<MtResult<?>>>();
    for (var res : results) {
      var sql = res.cmd.prepareSql().sqlP;
      idx.computeIfAbsent(sql, k -> new ArrayList<>()).add(res);
    }
    for (var e : idx.entrySet()) {
      var sql = e.getValue().get(0).cmd.sqlP;
      debug("Executing batch [{}]", sql);
      try (var ps = get().prepareStatement(sql)) {
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
    return results;
  }

  @Override public Connection get() {
    if (txFn != null) {
      return txFn.get();
    }
    try {
      return ds.getConnection();
    } catch (SQLException e) {
      throw generalError("Error getting database connection", e);
    }
  }

  @Override public boolean inTx() {
    return txFn != null;
  }

}
