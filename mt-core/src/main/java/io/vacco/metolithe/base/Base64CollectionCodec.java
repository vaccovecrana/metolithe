package io.vacco.metolithe.base;

import io.vacco.metolithe.core.EntityCollection;
import io.vacco.metolithe.spi.MtCollectionCodec;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Collection;
import static java.util.Objects.*;

public class Base64CollectionCodec implements MtCollectionCodec<String> {

  private static final String SIGMA_COL = "Σ:";

  @Override
  public String write(EntityCollection collection) {
    try {
      requireNonNull(collection);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(collection.value);
      oos.close();
      String b64 = Base64.getEncoder().encodeToString(baos.toByteArray());
      return String.format("%s%s", SIGMA_COL, b64);
    } catch (Exception e) { throw new IllegalStateException(e); }
  }

  @Override public Collection<?> read(String payload) {
    try {
      requireNonNull(payload);
      String b64 = payload.substring(2);
      byte [] data = Base64.getDecoder().decode(b64);
      ByteArrayInputStream bais = new ByteArrayInputStream(data);
      ObjectInputStream ois = new ObjectInputStream(bais);
      Object target = ois.readObject();
      ois.close();
      return (Collection<?>) target;
    } catch (Exception e) { throw new IllegalStateException(e); }
  }

  @Override
  public Collection<?> extract(ResultSet resultSet, Integer index) throws SQLException {
    String payload = resultSet.getString(index);
    return read(payload);
  }

  @Override public String getTargetSqlType() { return "varchar(4096)"; }
}
