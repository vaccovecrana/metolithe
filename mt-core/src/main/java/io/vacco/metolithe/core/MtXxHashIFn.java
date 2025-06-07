package io.vacco.metolithe.core;

import static io.vacco.metolithe.hashing.MtXxHash.*;
import static java.util.Objects.requireNonNull;

public class MtXxHashIFn implements MtIdFn<Integer> {

  private final int seed;

  public MtXxHashIFn(int seed) {
    this.seed = seed;
  }

  public MtXxHashIFn() {
    this.seed = DEFAULT_SEED;
  }

  @Override public Integer apply(Object[] parts) {
    return MtUtil.toStringConcat(requireNonNull(parts))
      .map(ba -> hash32(ba, 0, ba.length, this.seed))
      .orElseThrow(IllegalStateException::new);
  }

  @Override public Class<Integer> getIdType() {
    return Integer.class;
  }

}
