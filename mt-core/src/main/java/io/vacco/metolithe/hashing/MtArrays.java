package io.vacco.metolithe.hashing;

import java.util.*;
import static java.util.Arrays.*;

public class MtArrays {

  public static byte[] concat(byte[] first, byte[] second) {
    byte[] result = copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  public static Optional<byte[]> toStringConcat(Object ... vals) {
    return stream(vals)
        .filter(Objects::nonNull)
        .map(Object::toString)
        .map(String::getBytes)
        .reduce(MtArrays::concat);
  }
}
