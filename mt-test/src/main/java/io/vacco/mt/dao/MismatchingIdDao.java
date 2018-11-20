package io.vacco.mt.dao;

import io.vacco.metolithe.core.*;
import io.vacco.mt.schema.invalid.MismatchingIdEntity;
import org.codejargon.fluentjdbc.api.FluentJdbc;

public class MismatchingIdDao extends BaseUpdateDao<MismatchingIdEntity, Long> {
  public MismatchingIdDao(FluentJdbc jdbc, String sourceSchema) {
    super(jdbc, sourceSchema, new EntityDescriptor<>(
        MismatchingIdEntity.class, EntityDescriptor.CaseFormat.KEEP_CASE, null
    ), new Murmur3LongGenerator());
  }
}
