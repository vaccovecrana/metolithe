package io.vacco.metolithe.core;

public class MtException {

  public static class MtMissingIdException extends RuntimeException {
    public MtMissingIdException(Object field) {
      super(field == null ? "" : field.toString());
    }
  }

}
