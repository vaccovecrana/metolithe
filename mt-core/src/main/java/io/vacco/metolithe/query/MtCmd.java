package io.vacco.metolithe.query;

import java.sql.*;
import java.util.*;

import static io.vacco.metolithe.core.MtErr.*;

public class MtCmd {

  private final Map<String, Object> params = new LinkedHashMap<>();
  private final List<Object>        values = new ArrayList<>();

  public        String sqlP;
  private final String sql;
  public  int   rowCount = -1;

  MtCmd(String sql) {
    this.sql = sql;
  }

  public MtCmd param(String name, Object value) {
    if (value instanceof Enum) {
       params.put(name, value.toString());
    } else {
      params.put(name, value);
    }
    return this;
  }

  public MtCmd prepareSql() {
    sqlP = sql;
    for (var param : params.keySet()) {
      sqlP = sqlP.replace(":" + param, "?");
      values.add(params.get(param));
    }
    return this;
  }

  public void fill(PreparedStatement ps) throws SQLException {
    for (int i = 0; i < values.size(); i++) {
      ps.setObject(i + 1, values.get(i));
    }
  }

  public MtCmd executeOn(Connection conn) {
    try (var ps = conn.prepareStatement(prepareSql().sqlP)) {
      this.fill(ps);
      this.rowCount = ps.executeUpdate();
      return this;
    } catch (SQLException e) {
      throw badSql(false, sql, e);
    }
  }

  public MtCmd executeOn(MtConn connFn) {
    try (var conn = connFn.get()) {
      return executeOn(conn);
    } catch (SQLException e) {
      throw badSql(false, sql, e);
    }
  }

  public <T> List<T> list(MtMapper<T> mapper, MtConn connFn) {
    try (var conn = connFn.get();
         var ps   = conn.prepareStatement(prepareSql().sqlP)) {
      this.fill(ps);
      var rs = ps.executeQuery();
      var results = new ArrayList<T>();
      while (rs.next()) {
        results.add(mapper.map(rs));
      }
      return results;
    } catch (SQLException e) {
      throw badSql(true, sql, e);
    }
  }

  public <T> Optional<T> one(MtMapper<T> mapper, MtConn connFn) {
    return list(mapper, connFn).stream().findFirst();
  }

}
