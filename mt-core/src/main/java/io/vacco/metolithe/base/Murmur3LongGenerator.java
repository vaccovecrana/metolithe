package io.vacco.metolithe.base;

import io.vacco.metolithe.spi.MtIdGenerator;
import io.vacco.metolithe.util.ArrayConcat;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class Murmur3LongGenerator implements MtIdGenerator<Long> {

  private final int seed;

  public Murmur3LongGenerator() { this.seed = Murmur3.DEFAULT_SEED; }
  public Murmur3LongGenerator(int seed) { this.seed = seed; }

  @Override public Long apply(Object... parts) { // TODO implement int generator code as well
    Objects.requireNonNull(parts);
    Optional<byte []> oba = Arrays.stream(parts)
        .filter(Objects::nonNull)
        .map(Object::toString)
        .map(String::getBytes).reduce(ArrayConcat::apply);
    return oba
        .map(ba -> Murmur3.hash64(ba, 0, ba.length, this.seed))
        .orElse(defaultValue());
  }

  @Override public Long defaultValue() { return 0L; }
}