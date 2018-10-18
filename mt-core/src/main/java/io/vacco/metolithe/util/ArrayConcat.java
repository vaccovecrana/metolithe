package io.vacco.metolithe.util;

import java.util.Arrays;

public class ArrayConcat {

  public static byte[] apply(byte[] first, byte[] second) {
    byte[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

}
