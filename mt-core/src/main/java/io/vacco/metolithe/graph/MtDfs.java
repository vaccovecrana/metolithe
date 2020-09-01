package io.vacco.metolithe.graph;

import java.util.*;
import java.util.function.Consumer;

public class MtDfs {

  public static <T> void apply(MtVtx<T> v, MtGrph<T> g, Set<MtVtx<T>> visited,
                               Consumer<MtVtx<T>> preCn, Consumer<MtVtx<T>> postCn) {
    if (!visited.contains(v)) {
      visited.add(v);
      if (preCn != null) preCn.accept(v);
      g.edg.stream()
          .filter(e -> e.src.equals(v)).map(e -> e.dst)
          .forEach(ev -> apply(ev, g, visited, preCn, postCn));
      if (postCn != null) postCn.accept(v);
    }
  }

  public static <T> void apply(MtGrph<T> graph, Consumer<MtVtx<T>> preCn, Consumer<MtVtx<T>> postCn) {
    Set<MtVtx<T>> visited = new HashSet<>();
    for (MtVtx<T> vtx : graph.vtx) {
      apply(vtx, graph, visited, preCn, postCn);
    }
  }
}
