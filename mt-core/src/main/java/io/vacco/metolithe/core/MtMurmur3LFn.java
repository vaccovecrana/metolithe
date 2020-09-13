package io.vacco.metolithe.core;

import static io.vacco.metolithe.hashing.MtArrays.*;
import static io.vacco.metolithe.hashing.MtMurmur3.*;
import static java.util.Objects.requireNonNull;

public class MtMurmur3LFn implements MtIdFn<Long> {

  private final int seed;

  public MtMurmur3LFn() { this.seed = DEFAULT_SEED; }
  public MtMurmur3LFn(int seed) { this.seed = seed; }

  @Override public Long apply(Object... parts) {
    return toStringConcat(requireNonNull(parts))
        .map(ba -> hash64(ba, 0, ba.length, this.seed))
        .orElseThrow(IllegalStateException::new);
  }

  @Override public Class<Long> getIdType() { return Long.class; }
}
