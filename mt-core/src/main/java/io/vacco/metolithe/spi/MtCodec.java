package io.vacco.metolithe.spi;

import javax.validation.constraints.NotNull;

public interface MtCodec {
  boolean isEncoded(@NotNull String input);
  <T> String encode(@NotNull T input, @NotNull Class<?> targetClass);
  <T> T decode(@NotNull String input);
}
