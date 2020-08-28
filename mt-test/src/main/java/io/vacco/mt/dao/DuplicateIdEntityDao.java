package io.vacco.mt.dao;

import io.vacco.metolithe.core.BaseUpdateDao;
import io.vacco.metolithe.core.EntityDescriptor;
import io.vacco.metolithe.base.Murmur3LongGenerator;
import io.vacco.mt.schema.invalid.DuplicateIdEntity;
import org.codejargon.fluentjdbc.api.FluentJdbc;

public class DuplicateIdEntityDao extends BaseUpdateDao<DuplicateIdEntity, Long> {

  public DuplicateIdEntityDao(FluentJdbc jdbc, String sourceSchema) {
    super(jdbc, sourceSchema, new EntityDescriptor<>(
        DuplicateIdEntity.class, EntityDescriptor.CaseFormat.KEEP_CASE
    ), new Murmur3LongGenerator());
  }
}
