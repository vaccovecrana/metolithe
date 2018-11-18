package io.vacco.metolithe.spi;

import org.codejargon.fluentjdbc.api.mapper.ObjectMapperRsExtractor;
import java.util.Collection;
import java.util.function.Function;

public interface MtCollectionCodec<O> extends Function<Collection<?>, O>, ObjectMapperRsExtractor<Collection<?>> {
  O write(Collection<?> payload);
  Collection<?> read(O payload);
  String getTargetSqlType();
  @Override default O apply(Collection<?> ts) { return write(ts); }
}
