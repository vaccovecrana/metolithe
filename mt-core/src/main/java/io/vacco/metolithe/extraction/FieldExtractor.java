package io.vacco.metolithe.extraction;

import java.lang.reflect.Field;

public interface FieldExtractor<T> {
  Object doExtract(T in, Field f);
}
