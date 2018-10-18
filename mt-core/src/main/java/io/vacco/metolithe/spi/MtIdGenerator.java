package io.vacco.metolithe.spi;

public interface MtIdGenerator<T> {
  T apply(Object ... parts);
  T defaultValue();
}
