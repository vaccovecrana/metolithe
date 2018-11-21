package io.vacco.mt.dao;

import io.vacco.metolithe.core.BaseUpdateDao;
import io.vacco.metolithe.core.EntityDescriptor;
import io.vacco.metolithe.core.Murmur3LongGenerator;
import io.vacco.metolithe.util.Base64CollectionCodec;
import io.vacco.mt.schema.invalid.InvalidCollectionTypeEntity;
import org.codejargon.fluentjdbc.api.FluentJdbc;

public class InvalidCollectionTypeDao extends BaseUpdateDao<InvalidCollectionTypeEntity, Long> {
  public InvalidCollectionTypeDao(FluentJdbc jdbc, String sourceSchema) {
    super(jdbc, sourceSchema, new EntityDescriptor<>(
        InvalidCollectionTypeEntity.class, EntityDescriptor.CaseFormat.KEEP_CASE, new Base64CollectionCodec()
    ), new Murmur3LongGenerator());
  }
}
