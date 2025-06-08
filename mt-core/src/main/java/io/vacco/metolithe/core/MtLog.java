package io.vacco.metolithe.core;

import java.util.Objects;
import java.util.function.BiConsumer;

public class MtLog {

  public static BiConsumer<String, Object[]> logFn;

  public static void setLogger(BiConsumer<String, Object[]> logFn) {
    MtLog.logFn = Objects.requireNonNull(logFn);
  }

  public static void info(String fmt, Object ... args) {
    if (logFn != null) {
      logFn.accept(fmt, args);
    }
  }

}
