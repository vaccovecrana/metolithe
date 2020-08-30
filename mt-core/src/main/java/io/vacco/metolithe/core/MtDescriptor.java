package io.vacco.metolithe.core;

import java.util.*;
import java.util.stream.Collectors;

public class MtDescriptor {

  private final Class<?> target;
  private final Set<MtFieldDescriptor> fieldSet;

  public MtDescriptor(Class<?> entity) {
    this.target = Objects.requireNonNull(entity);
    this.fieldSet = Arrays.stream(entity.getFields())
        .map(MtFieldDescriptor::new).collect(Collectors.toSet());
  }

  @Override
  public String toString() {
    return String.format(
        "<%s>%s", target.getSimpleName(), fieldSet
    );
  }
}
