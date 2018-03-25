package io.vacco.metolithe.core;

public interface UnsafeSupplier<T> {
  T get() throws Exception;
}
