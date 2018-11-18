package io.vacco.mt.dao;

import io.vacco.metolithe.core.*;
import io.vacco.mt.schema.invalid.InvalidEntity;
import org.codejargon.fluentjdbc.api.FluentJdbc;

public class InvalidEntityDao extends BaseUpdateDao<InvalidEntity, Long> {
  public InvalidEntityDao(FluentJdbc jdbc, String sourceSchema) {
    super(jdbc, sourceSchema, new EntityDescriptor<>(
        InvalidEntity.class, EntityDescriptor.CaseFormat.KEEP_CASE, null
    ), new Murmur3LongGenerator());
  }
}
