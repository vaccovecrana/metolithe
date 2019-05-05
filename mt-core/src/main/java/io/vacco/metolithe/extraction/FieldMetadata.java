package io.vacco.metolithe.extraction;

import io.vacco.metolithe.annotations.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Stream;

public class FieldMetadata {

  public final Field field;

  public FieldMetadata(Field f) {
    this.field = Objects.requireNonNull(f);
    this.field.setAccessible(true);
  }

  public Optional<MtAttribute> hasNotNull() {
    Optional<MtAttribute> mta = hasAttribute();
    if (mta.isPresent() && !mta.get().nil()) { return mta; }
    return Optional.empty();
  }

  public boolean isEntityField() {
    return hasPrimaryKey().isPresent()
        || hasAttribute().isPresent()
        || hasCollection().isPresent();
  }

  public Optional<MtId> hasPrimaryKey() {
    return scan(field)
        .filter(an0 -> an0.annotationType() == MtId.class)
        .map(an0 -> (MtId) an0).findFirst();
  }

  public Optional<MtId> hasPrimaryKeyOf(Class<?> target) {
    Optional<MtId> id = hasPrimaryKey();
    if (field.getDeclaringClass() == target) return id;
    return Optional.empty();
  }

  public Optional<MtAttribute> hasAttribute() {
    return scan(field)
        .filter(an0 -> an0.annotationType() == MtAttribute.class)
        .map(an0 -> (MtAttribute) an0)
        .findFirst();
  }

  public Optional<MtCollection> hasCollection() {
    return scan(field)
        .filter(an0 -> an0.annotationType() == MtCollection.class)
        .map(an0 -> (MtCollection) an0)
        .findFirst();
  }

  public Optional<MtIndex> hasIndex() {
    return scan(field)
        .filter(an0 -> an0.annotationType() == MtIndex.class)
        .map(an0 -> (MtIndex) an0).findFirst();
  }

  public Optional<MtIdGroup> hasIdGroup() {
    return scan(field)
        .filter(an0 -> an0.annotationType() == MtIdGroup.class)
        .map(an0 -> (MtIdGroup) an0).findFirst();
  }

  public Optional<MtAttribute> hasLength() {
    return scan(field)
        .filter(a0 -> a0.annotationType() == MtAttribute.class)
        .map(an0 -> (MtAttribute) an0)
        .filter(at0 -> at0.len() > 0).findFirst();
  }

  public Optional<MtIndex> hasIndexOf(Class<?> target) {
    Optional<MtIndex> idx = hasIndex();
    if (idx.isPresent() && field.getDeclaringClass() == target) return idx;
    return Optional.empty();
  }

  public boolean isValidCollectionField() {
    if (hasIndex().isPresent()) return false;
    if (hasAttribute().isPresent()) return false;
    return hasCollection().isPresent();
  }

  public Annotation [] getRawAnnotations() { return scan(field).toArray(Annotation[]::new); }

  public Optional<Class<?>> getCollectionType() {
    if (!hasCollection().isPresent()) return Optional.empty();
    ParameterizedType t = (ParameterizedType) field.getGenericType();
    Class<?> typeClass = (Class<?>) t.getActualTypeArguments()[0];
    return Optional.of(typeClass);
  }

  private Stream<Annotation> scan(Field f) {
    return Arrays.stream(f.getDeclaredAnnotations()).flatMap(an0 -> {
      ArrayList<Annotation> fm = new ArrayList<>();
      fm.add(an0);
      fm.addAll(Arrays.asList(an0.annotationType().getDeclaredAnnotations()));
      return fm.stream();
    });
  }

  @Override public boolean equals(Object obj) {
    if (!(obj instanceof FieldMetadata)) return false;
    FieldMetadata fm0 = (FieldMetadata) obj;
    return this.field.getName().equals(fm0.field.getName());
  }

  @Override public int hashCode() { return this.field.hashCode(); }

  @Override public String toString() {
    return field.getName();
  }
}
