package io.vacco.metolithe.graph;

import java.util.Objects;

public class MtEdg<T> implements Comparable<MtEdg<T>> {

  public final MtVtx<T> src;
  public final MtVtx<T> dst;

  public MtEdg(MtVtx<T> src, MtVtx<T> dst) {
    this.src = Objects.requireNonNull(src);
    this.dst = Objects.requireNonNull(dst);
  }

  public MtEdg<T> reverse() {
    return new MtEdg<>(dst, src);
  }

  private String label() {
    return String.format("%s%s", this.src.id, this.dst.id);
  }

  @Override public int compareTo(MtEdg<T> tEdg) {
    return label().compareTo(tEdg.label());
  }

  @Override public String toString() {
    return String.format("[%s -> %s]", src.id, dst.id);
  }
}
