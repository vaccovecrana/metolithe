package io.vacco.metolithe.core;

import java.util.Collection;
import java.util.Objects;

public class EntityCollection<T, V> {
  public EntityDescriptor<T> descriptor;
  public Collection<V> value;

  public EntityCollection<T, V> with(EntityDescriptor<T> d, Collection<V> c) {
    this.descriptor = Objects.requireNonNull(d);
    this.value = Objects.requireNonNull(c);
    return this;
  }
}
