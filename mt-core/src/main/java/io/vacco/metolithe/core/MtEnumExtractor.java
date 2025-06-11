package io.vacco.metolithe.core;

import static io.vacco.metolithe.core.MtErr.*;
import org.codejargon.fluentjdbc.api.mapper.ObjectMapperRsExtractor;
import java.sql.ResultSet;

import static java.util.Objects.*;

public class MtEnumExtractor<T extends Enum<T>> implements ObjectMapperRsExtractor<T> {

  private final Class<T> target;

  public MtEnumExtractor(Class<T> target) {
    this.target = requireNonNull(target);
  }

  @Override public T extract(ResultSet rs, Integer idx) {
    try {
      var enumValue = rs.getString(idx);
      if (enumValue != null) {
        return Enum.valueOf(target, enumValue);
      }
      return null;
    } catch (Exception e) {
      throw badEnumExtraction(target.getCanonicalName(), e);
    }
  }
}