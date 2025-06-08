package io.vacco.metolithe.id;

public class MtNoopIdFn implements MtIdFn<Void> {
  @Override public Void apply(Object[] objects) {
    return null;
  }
  @Override public Class<Void> getIdType() {
    return Void.class;
  }
}
