package io.vacco.metolithe.graph;

import java.util.*;
import static java.util.stream.Collectors.*;

public class MtGrph<T> {

  public final Set<MtVtx<T>> vtx = new LinkedHashSet<>();
  public final Set<MtEdg<T>> edg = new TreeSet<>();

  public MtGrph<T> addEdge(MtVtx<T> from, MtVtx<T> to) {
    this.vtx.add(from);
    this.vtx.add(to);
    MtEdg<T> e = new MtEdg<>(from, to);
    edg.add(e);
    return this;
  }

  public MtGrph<T> reverse() {
    MtGrph<T> g0 = new MtGrph<>();
    g0.vtx.addAll(this.vtx);
    g0.edg.addAll(this.edg.stream().map(MtEdg::reverse).collect(toList()));
    return g0;
  }
}
