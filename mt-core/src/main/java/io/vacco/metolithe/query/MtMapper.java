package io.vacco.metolithe.query;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface MtMapper<T> {

  T map(ResultSet rs) throws SQLException;

}
