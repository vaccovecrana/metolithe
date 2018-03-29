package io.vacco.metolithe.spi;

public interface MtCodec {
  boolean isEncoded(String input);
  <T> String encode(T input, Class<?> targetClass);
  <T> T decode(String input);
}
