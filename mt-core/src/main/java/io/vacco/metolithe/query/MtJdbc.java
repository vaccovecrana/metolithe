package io.vacco.metolithe.query;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

import static io.vacco.metolithe.core.MtLog.debug;
import static io.vacco.metolithe.core.MtErr.*;

public class MtJdbc implements MtConn {

  private static final Map<Thread, MtConn> txIdx = new ConcurrentHashMap<>();

  private final DataSource ds;

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
      txIdx.put(Thread.currentThread(), tx);
      tx.start(conn -> txFn.accept(tx, conn));
    } catch (Exception e) {
      throw generalError("Transaction failed", e);
    } finally {
      txIdx.remove(Thread.currentThread());
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

  private MtConn getTxFn() {
    return txIdx.get(Thread.currentThread());
  }

  @Override public Connection get() {
    var txFn = getTxFn();
    if (txFn != null && txFn.get() != null) {
      return txFn.get();
    }
    try {
      return ds.getConnection();
    } catch (SQLException e) {
      throw generalError("Error getting database connection", e);
    }
  }

  @Override public boolean inTx() {
    return getTxFn() != null;
  }

}
