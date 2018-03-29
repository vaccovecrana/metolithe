package io.vacco.metolithe.spi;

public interface UnsafeSupplier<T> {
  T get() throws Exception;
}
