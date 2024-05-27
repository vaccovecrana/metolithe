package io.vacco.metolithe.util;

import java.util.List;
import java.util.Objects;

public class MtPage1<T, K1> {

  public long     size;
  public List<T>  items;
  public K1       nx1;

  public static <T, K1> MtPage1<T, K1> of(long size, List<T> items, K1 nx1) {
    var p = new MtPage1<T, K1>();
    p.size = size;
    p.items = Objects.requireNonNull(items);
    p.nx1 = nx1;
    return p;
  }
}
