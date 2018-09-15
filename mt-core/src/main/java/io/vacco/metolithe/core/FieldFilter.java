package io.vacco.metolithe.core;

import io.vacco.metolithe.annotations.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

public final class FieldFilter {

  public static Optional<MtId> hasPrimaryKey(Field f) {
    return assignedAndParentAnnotationsOf(f)
        .filter(an0 -> an0.annotationType() == MtId.class)
        .map(an0 -> (MtId) an0).findFirst();
  }

  public static Optional<MtAttribute> hasAttribute(Field f) {
    return assignedAndParentAnnotationsOf(f)
        .filter(an0 -> an0.annotationType() == MtAttribute.class)
        .map(an0 -> (MtAttribute) an0)
        .findFirst();
  }

  public static Optional<MtAttribute> hasNotNull(Field f) {
    Optional<MtAttribute> mta = hasAttribute(f);
    if (mta.isPresent() && !mta.get().nil()) { return mta; }
    return Optional.empty();
  }

  public static Optional<MtIndex> hasIndex(Field f) {
    return assignedAndParentAnnotationsOf(f)
        .filter(an0 -> an0.annotationType() == MtIndex.class)
        .map(an0 -> (MtIndex) an0).findFirst();
  }

  public static boolean isOwnId(Class<?> target, Field f) {
    Optional<MtId> id = hasPrimaryKey(f);
    return id.isPresent() && f.getDeclaringClass() == target;
  }

  public static boolean isOwnIndex(Class<?> target, Field f) {
    Optional<MtIndex> idx = hasIndex(f);
    return idx.isPresent() && f.getDeclaringClass() == target;
  }

  private static Stream<Annotation> assignedAndParentAnnotationsOf(Field f) {
    return Arrays.stream(f.getDeclaredAnnotations()).flatMap(an0 -> {
      ArrayList<Annotation> fm = new ArrayList<>();
      fm.add(an0);
      fm.addAll(Arrays.asList(an0.annotationType().getDeclaredAnnotations()));
      return fm.stream();
    });
  }
}
