package io.vacco.metolithe.core;

import io.vacco.metolithe.annotations.MtVarchar;
import java.util.Optional;
import static java.lang.String.*;

public class MtTypeMapper {

  public static final int ENUM_VARCHAR_LENGTH = 64;

  public static Class<?> toWrapperClass(Class<?> type) {
    if (!type.isPrimitive()) return type;
    else if (int.class.equals(type)) return Integer.class;
    else if (double.class.equals(type)) return Double.class;
    else if (char.class.equals(type)) return Character.class;
    else if (boolean.class.equals(type)) return Boolean.class;
    else if (long.class.equals(type)) return Long.class;
    else if (float.class.equals(type)) return Float.class;
    else if (short.class.equals(type)) return Short.class;
    else if (byte.class.equals(type)) return Byte.class;
    else throw new IllegalArgumentException("Not a primitive type class: " + type.getName());
  }

  public static String sqlTypeOf(MtFieldDescriptor fd) {
    Class<?> wt0 = toWrapperClass(fd.getField().getType());
    if (wt0 == Boolean.class) { return "boolean"; }
    if (wt0 == String.class) {
      Optional<MtVarchar> maxSize = fd.get(MtVarchar.class);
      if (maxSize.isPresent()) { return format("varchar(%s)", maxSize.get().value()); }
      return "varchar";
    }
    if (wt0 == Integer.class) { return "int"; }
    if (wt0 == Long.class) { return "bigint"; }
    if (wt0 == Double.class) { return "double"; }
    if (Enum.class.isAssignableFrom(fd.getField().getType())) {
      return format("varchar(%s)", ENUM_VARCHAR_LENGTH);
    }
    throw new IllegalArgumentException(format("Unable to map type/annotation set: %s", fd));
  }
}