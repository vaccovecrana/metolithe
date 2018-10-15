package io.vacco.metolithe.spi;

public interface MtIdGenerator<T> {
  Class<T> targetType();
  T apply(Object ... parts);
}
