package io.vacco.metolithe.codegen.liquibase;

import io.vacco.metolithe.annotations.MtAttribute;

import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class TypeMapper {

  public static String resolveSqlType(Class<?> jt0, Annotation... annotations) {
    requireNonNull(jt0);
    requireNonNull(annotations);
    if (jt0 == Boolean.class || jt0 == boolean.class) { return "boolean"; }
    if (jt0 == String.class) {
      Optional<Size> maxSize = hasSize(annotations);
      if (maxSize.isPresent()) { return format("varchar(%s)", maxSize.get().max()); }
      return "varchar";
    }
    if (Long.class == jt0 || long.class == jt0 || Integer.class == jt0 || int.class == jt0) { return "bigint"; }
    if (Double.class == jt0 || double.class == jt0) { return "double"; }
    if (Enum.class.isAssignableFrom(jt0)) {
      if (hasSize(annotations).isPresent()) {
        throw new IllegalArgumentException("Please annotate Enum field size with MtAttribute annotations.");
      }
      Optional<MtAttribute> maxLength = hasMaxLength(annotations);
      if (maxLength.isPresent()) { return format("varchar(%s)", maxLength.get().maxByteLength()); }
    }
    if (Collection.class.isAssignableFrom(jt0)) {
      Optional<MtAttribute> maxLength = hasMaxLength(annotations);
      if (maxLength.isPresent()) { return format("varchar(%s)", maxLength.get().maxByteLength()); }
    }
    throw new IllegalArgumentException(format("Unable to map type/annotation set: [%s], [%s]", jt0, Arrays.asList(annotations)));
  }

  private static Optional<Size> hasSize(Annotation ... annotations) {
    return Arrays.stream(annotations)
        .filter(a0 -> a0.annotationType() == Size.class).map(a0 -> (Size) a0)
        .filter(sa0 -> sa0.max() > 0).findFirst();
  }

  private static Optional<MtAttribute> hasMaxLength(Annotation ... annotations) {
    return Arrays.stream(annotations)
        .filter(a0 -> a0.annotationType() == MtAttribute.class).map(an0 -> (MtAttribute) an0)
        .filter(at0 -> at0.maxByteLength() > 0).findFirst();
  }

}
