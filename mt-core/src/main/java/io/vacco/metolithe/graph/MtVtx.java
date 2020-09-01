package io.vacco.metolithe.graph;

import java.util.*;

public class MtVtx<T> {

  public final String id;
  public T data;

  public MtVtx(String id, T data) {
    this.id = Objects.requireNonNull(id);
    this.data = Objects.requireNonNull(data);
  }

  @Override public boolean equals(Object o) {
    return o instanceof MtVtx && this.id.equals(((MtVtx<?>) o).id);
  }

  @Override public int hashCode() {
    return id.hashCode();
  }
  @Override public String toString() {
    return String.format("V{%s}", id);
  }
}
