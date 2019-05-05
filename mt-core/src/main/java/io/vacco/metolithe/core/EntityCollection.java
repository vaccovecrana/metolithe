package io.vacco.metolithe.core;

import java.util.Collection;
import java.util.Objects;

public class EntityCollection {

  public Collection value;
  public Class valueType;

  public EntityCollection with(Collection c, Class t) {
    this.value = Objects.requireNonNull(c);
    this.valueType = Objects.requireNonNull(t);
    return this;
  }
}
