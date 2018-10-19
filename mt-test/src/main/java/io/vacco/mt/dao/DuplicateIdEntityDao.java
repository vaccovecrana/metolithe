package io.vacco.mt.dao;

import io.vacco.metolithe.core.BaseUpdateDao;
import io.vacco.metolithe.core.EntityDescriptor;
import io.vacco.metolithe.core.Murmur3LongGenerator;
import io.vacco.mt.schema.invalid.DuplicateIdEntity;
import org.codejargon.fluentjdbc.api.FluentJdbc;

public class DuplicateIdEntityDao extends BaseUpdateDao<DuplicateIdEntity, Long> {

  public DuplicateIdEntityDao(FluentJdbc jdbc, String sourceSchema) {
    super(DuplicateIdEntity.class, jdbc, sourceSchema,
        EntityDescriptor.CaseFormat.KEEP_CASE, new Murmur3LongGenerator());
  }
}
