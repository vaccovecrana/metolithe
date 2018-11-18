package io.vacco.metolithe.extraction;

import io.vacco.metolithe.annotations.MtAttribute;
import io.vacco.metolithe.spi.MtCollectionCodec;
import io.vacco.metolithe.util.TypeUtil;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class TypeMapper {

  private final MtCollectionCodec<?> collectionCodec;

  public TypeMapper() { this.collectionCodec = null; }
  public TypeMapper(MtCollectionCodec<?> collectionCodec) {
    this.collectionCodec = requireNonNull(collectionCodec);
  }

  public String resolveSqlType(Class<?> jt0, Annotation ... annotations) {
    requireNonNull(jt0);
    requireNonNull(annotations);
    Class<?> wt0 = TypeUtil.toWrapperClass(jt0);
    if (wt0 == Boolean.class) { return "boolean"; }
    if (wt0 == String.class) {
      Optional<MtAttribute> maxSize = hasLength(annotations);
      if (maxSize.isPresent()) { return format("varchar(%s)", maxSize.get().len()); }
      return "varchar";
    }
    if (wt0 == Long.class || wt0 == Integer.class) { return "bigint"; }
    if (wt0 == Double.class) { return "double"; }
    if (Enum.class.isAssignableFrom(jt0)) {
      Optional<MtAttribute> maxLength = hasLength(annotations);
      if (maxLength.isPresent()) { return format("varchar(%s)", maxLength.get().len()); }
    }
    if (collectionCodec != null && Collection.class.isAssignableFrom(jt0)) {
      return collectionCodec.getTargetSqlType();
    }
    throw new IllegalArgumentException(format("Unable to map type/annotation set: [%s], [%s]", jt0, Arrays.asList(annotations)));
  }

  private static Optional<MtAttribute> hasLength(Annotation ... annotations) {
    return Arrays.stream(annotations)
        .filter(a0 -> a0.annotationType() == MtAttribute.class).map(an0 -> (MtAttribute) an0)
        .filter(at0 -> at0.len() > 0).findFirst();
  }
}
