package io.vacco.metolithe.extraction;

import io.vacco.metolithe.annotations.MtAttribute;
import io.vacco.metolithe.annotations.MtCollection;
import io.vacco.metolithe.util.TypeUtil;
import java.util.*;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class TypeMapper {

  public String resolveSqlType(FieldMetadata fm) {
    requireNonNull(fm);
    Class<?> wt0 = TypeUtil.toWrapperClass(fm.field.getType());
    if (wt0 == Boolean.class) { return "boolean"; }
    if (wt0 == String.class) {
      Optional<MtAttribute> maxSize = fm.hasLength();
      if (maxSize.isPresent()) { return format("varchar(%s)", maxSize.get().len()); }
      return "varchar";
    }
    if (wt0 == Long.class || wt0 == Integer.class) { return "bigint"; }
    if (wt0 == Double.class) { return "double"; }
    if (Enum.class.isAssignableFrom(fm.field.getType())) {
      Optional<MtAttribute> maxLength = fm.hasLength();
      if (maxLength.isPresent()) { return format("varchar(%s)", maxLength.get().len()); }
    }
    if (Collection.class.isAssignableFrom(fm.field.getType())) {
      Optional<MtCollection> col = fm.hasCollection();
      if (col.isPresent()) { return col.get().sqlType(); }
    }
    throw new IllegalArgumentException(format("Unable to map type/annotation set: [%s], [%s]",
        fm.field, Arrays.asList(fm.getRawAnnotations())));
  }
}
