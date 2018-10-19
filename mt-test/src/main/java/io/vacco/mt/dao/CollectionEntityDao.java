package io.vacco.mt.dao;

import io.vacco.metolithe.core.BaseUpdateDao;
import io.vacco.metolithe.core.EntityDescriptor;
import io.vacco.metolithe.core.Murmur3LongGenerator;
import io.vacco.mt.schema.invalid.CollectionEntity;
import org.codejargon.fluentjdbc.api.FluentJdbc;

public class CollectionEntityDao extends BaseUpdateDao<CollectionEntity, Long> {
  public CollectionEntityDao(FluentJdbc jdbc, String sourceSchema) {
    super(CollectionEntity.class, jdbc, sourceSchema,
        EntityDescriptor.CaseFormat.KEEP_CASE, new Murmur3LongGenerator());
  }
}
