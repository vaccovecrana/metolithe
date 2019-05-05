package io.vacco.mt.dao;

import io.vacco.metolithe.core.BaseUpdateDao;
import io.vacco.metolithe.core.EntityDescriptor;
import io.vacco.metolithe.base.Murmur3LongGenerator;
import io.vacco.metolithe.base.Base64CollectionCodec;
import io.vacco.mt.schema.valid.CollectionEntity;
import org.codejargon.fluentjdbc.api.FluentJdbc;

public class CollectionEntityDao extends BaseUpdateDao<CollectionEntity, Long> {
  public CollectionEntityDao(FluentJdbc jdbc, String sourceSchema) {
    super(jdbc, sourceSchema, new EntityDescriptor<>(
        CollectionEntity.class, EntityDescriptor.CaseFormat.KEEP_CASE,
        new Base64CollectionCodec()), new Murmur3LongGenerator());
  }
}
