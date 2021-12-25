package io.vacco.metolithe.core;

import java.util.List;

public class MtPage<T, V> {
  public long     totalSize;
  public List<T>  data;
  public V        next;
}
