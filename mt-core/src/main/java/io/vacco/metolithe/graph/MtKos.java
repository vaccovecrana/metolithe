package io.vacco.metolithe.graph;

import java.util.*;

/**
 * <a href="https://www.cs.princeton.edu/courses/archive/fall12/cos226/lectures/42DirectedGraphs.pdf" />
 */
public class MtKos {

  public static <T> Map<Integer, List<MtVtx<T>>> apply(MtGrph<T> g) {
    List<MtVtx<T>> rpo = new ArrayList<>();
    MtDfs.apply(g.reverse(), null, rpo::add);
    Collections.reverse(rpo);

    final int[] count = {0};
    Set<MtVtx<T>> visited = new HashSet<>();
    Map<Integer, List<MtVtx<T>>> levels = new HashMap<>();

    for (MtVtx<T> vr : rpo) {
      if (!visited.contains(vr)) {
        MtDfs.apply(vr, g, visited,
            v0 -> levels.computeIfAbsent(count[0], c -> new ArrayList<>()).add(v0),
            null);
        count[0] = count[0] + 1;
      }
    }
    return levels;
  }
}
