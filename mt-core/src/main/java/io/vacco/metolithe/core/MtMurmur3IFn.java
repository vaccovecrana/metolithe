package io.vacco.metolithe.core;

import static io.vacco.metolithe.hashing.MtArrays.*;
import static io.vacco.metolithe.hashing.MtMurmur3.*;
import static java.util.Objects.requireNonNull;

public class MtMurmur3IFn implements MtIdFn<Integer> {

  private final int seed;

  public MtMurmur3IFn() { this.seed = DEFAULT_SEED; }
  public MtMurmur3IFn(int seed) { this.seed = seed; }

  @Override public Integer apply(Object... parts) {
    return toStringConcat(requireNonNull(parts))
        .map(ba -> hash32(ba, 0, ba.length, this.seed))
        .orElseThrow(IllegalStateException::new);
  }

  @Override public Class<Integer> getIdType() { return Integer.class; }
}
