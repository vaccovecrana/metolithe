package io.vacco.metolithe.core;

import io.vacco.metolithe.spi.MtIdGenerator;
import io.vacco.metolithe.util.ArrayConcat;
import io.vacco.metolithe.util.Murmur3;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

// TODO add seed initialization parameter.
public class Murmur3LongGenerator implements MtIdGenerator<Long> {

  @Override public Long apply(Object... parts) {
    Objects.requireNonNull(parts);
    Optional<byte []> oba = Arrays.stream(parts)
        .filter(Objects::nonNull)
        .map(Object::toString)
        .map(String::getBytes).reduce(ArrayConcat::apply);
    return oba.map(Murmur3::hash64).orElse(defaultValue());
  }

  @Override public Long defaultValue() { return 0L; }
}
