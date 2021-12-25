package io.vacco.metolithe.core;

import io.vacco.metolithe.annotations.MtVarchar;
import java.util.Optional;

import static java.lang.String.*;

public class MtTypeMapper {

  public static final int ENUM_VARCHAR_LENGTH = 64;

  public static String sqlTypeOf(MtFieldDescriptor fd) {
    Class<?> wt0 = MtUtil.toWrapperClass(fd.getType());
    if (wt0 == Boolean.class) { return "boolean"; }
    if (wt0 == String.class) {
      Optional<MtVarchar> maxSize = fd.get(MtVarchar.class);
      return format("varchar(%s)", maxSize.get().value());
    }
    if (wt0 == Integer.class) { return "int"; }
    if (wt0 == Long.class) { return "bigint"; }
    if (wt0 == Double.class) { return "double"; }
    if (Enum.class.isAssignableFrom(fd.getType())) {
      return format("varchar(%s)", ENUM_VARCHAR_LENGTH);
    }
    throw new MtException.MtSqlTypeMappingException(fd);
  }
}
