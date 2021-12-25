package io.vacco.metolithe.core;

import java.util.*;
import java.util.function.Function;

import static java.util.Arrays.*;

public class MtUtil {

  public static Class<?> toWrapperClass(Class<?> type) {
    if (!type.isPrimitive()) return type;
    else if (int.class.equals(type)) { return Integer.class; }
    else if (double.class.equals(type)) { return Double.class; }
    else if (char.class.equals(type)) { return Character.class; }
    else if (boolean.class.equals(type)) { return Boolean.class; }
    else if (long.class.equals(type)) { return Long.class; }
    else if (float.class.equals(type)) { return Float.class; }
    else if (short.class.equals(type)) { return Short.class; }
    else if (byte.class.equals(type)) { return Byte.class; }
    return type;
  }

  public static byte[] concat(byte[] first, byte[] second) {
    byte[] result = copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  public static Optional<byte[]> fnConcat(Function<Object, String> idFn, Object ... values) {
    return stream(values)
        .filter(Objects::nonNull)
        .map(idFn)
        .map(String::getBytes)
        .reduce(MtUtil::concat);
  }

  public static Optional<byte[]> toStringConcat(Object ... values) {
    return fnConcat(Object::toString, values);
  }

}
