package io.vacco.metolithe.spi;

import io.vacco.metolithe.core.EntityCollection;
import org.codejargon.fluentjdbc.api.mapper.ObjectMapperRsExtractor;
import java.util.Collection;
import java.util.function.Function;

public interface MtCollectionCodec<O> extends Function<EntityCollection, O>, ObjectMapperRsExtractor<Collection<?>> {

  O write(EntityCollection collection);
  Collection<?> read(O payload);
  String getTargetSqlType();

  @Override default O apply(EntityCollection entityCollection) {
    return write(entityCollection);
  }
}
