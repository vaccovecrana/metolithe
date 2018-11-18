package io.vacco.metolithe.extraction;

import io.vacco.metolithe.annotations.*;
import java.lang.reflect.Field;
import java.util.*;

public final class FieldFilter {

  public static Optional<MtId> hasPrimaryKey(Field f) {
    return AnnotationExtractor.apply(f)
        .filter(an0 -> an0.annotationType() == MtId.class)
        .map(an0 -> (MtId) an0).findFirst();
  }

  public static Optional<MtId> hasOwnPrimaryKey(Class<?> target, Field f) {
    Optional<MtId> id = hasPrimaryKey(f);
    if (f.getDeclaringClass() == target) return id;
    return Optional.empty();
  }

  public static Optional<MtAttribute> hasAttribute(Field f) {
    return AnnotationExtractor.apply(f)
        .filter(an0 -> an0.annotationType() == MtAttribute.class)
        .map(an0 -> (MtAttribute) an0)
        .findFirst();
  }

  public static Optional<MtCollection> hasCollection(Field f) {
    return AnnotationExtractor.apply(f)
        .filter(an0 -> an0.annotationType() == MtCollection.class)
        .map(an0 -> (MtCollection) an0)
        .findFirst();
  }

  public static Optional<MtAttribute> hasNotNull(Field f) {
    Optional<MtAttribute> mta = hasAttribute(f);
    if (mta.isPresent() && !mta.get().nil()) { return mta; }
    return Optional.empty();
  }

  public static Optional<MtIndex> hasIndex(Field f) {
    return AnnotationExtractor.apply(f)
        .filter(an0 -> an0.annotationType() == MtIndex.class)
        .map(an0 -> (MtIndex) an0).findFirst();
  }

  public static Optional<MtIdGroup> hasIdGroup(Field f) {
    return AnnotationExtractor.apply(f)
        .filter(an0 -> an0.annotationType() == MtIdGroup.class)
        .map(an0 -> (MtIdGroup) an0).findFirst();
  }

  public static Optional<MtIndex> hasOwnIndex(Class<?> target, Field f) {
    Optional<MtIndex> idx = hasIndex(f);
    if (idx.isPresent() && f.getDeclaringClass() == target) return idx;
    return Optional.empty();
  }

  public static boolean isOwnPrimaryKey(Class<?> target, Field f) {
    return hasOwnPrimaryKey(target, f).isPresent();
  }

  public static boolean isOwnIndex(Class<?> target, Field f) {
    return hasOwnIndex(target, f).isPresent();
  }
}
