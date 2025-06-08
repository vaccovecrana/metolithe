package io.vacco.metolithe.changeset;

public class MtCol {

  public String name, type;
  public Boolean nullable, primaryKey;

  @Override public String toString() {
    return String.format(
      "%s %s [null: %b, pk: %b]",
      name, type, nullable, primaryKey
    );
  }

}
