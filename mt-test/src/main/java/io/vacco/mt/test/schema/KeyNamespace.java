package io.vacco.mt.test.schema;

import io.vacco.metolithe.annotations.*;

@MtEntity public class KeyNamespace {

  @MtPk public int id;

  @MtFk(ApiKey.class)
  @MtUnique(idx = 0, inPk = true)
  @MtNotNull
  public Integer kid;

  @MtFk(Namespace.class)
  @MtUnique(idx = 1, inPk = true)
  public Integer nsId;

  public static KeyNamespace of(Integer kid, Integer nsId) {
    var kn = new KeyNamespace();
    kn.kid = kid;
    kn.nsId = nsId;
    return kn;
  }

  @Override public String toString() {
    return String.format("%d, %d, %d", id, kid, nsId);
  }

}
