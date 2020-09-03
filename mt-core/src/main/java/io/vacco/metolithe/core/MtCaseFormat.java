package io.vacco.metolithe.core;

public enum MtCaseFormat {

  UPPER_CASE, LOWER_CASE, KEEP_CASE;

  public String of(String in) {
    if (in == null) return null;
    switch (this) {
      case LOWER_CASE: return in.toLowerCase();
      case UPPER_CASE: return in.toUpperCase();
    }
    return in;
  }
}
