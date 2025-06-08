package io.vacco.metolithe.id;

import java.util.function.Function;

public interface MtIdFn<I> extends Function<Object[], I> {
  Class<I> getIdType();
}
