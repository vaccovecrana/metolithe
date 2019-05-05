package io.vacco.metolithe.core;

import io.vacco.metolithe.annotations.*;
import io.vacco.metolithe.extraction.*;
import io.vacco.metolithe.spi.MtCollectionCodec;
import java.lang.reflect.Field;
import java.util.*;

import static java.util.Objects.*;
import static java.util.stream.Collectors.joining;

public class EntityDescriptor<T> implements FieldExtractor<T> {

  public enum CaseFormat { UPPER_CASE, LOWER_CASE, KEEP_CASE }
  public static final String COMMA_SPC = ", ";

  private final Class<T> target;
  private final Map<String, FieldMetadata> fields;
  private final Map<Field, FieldMetadata> fieldIndex;
  private final CaseFormat format;
  private final MtEntity entityAnnotation;
  private final IdMetadata<T> idMetadata;
  private final MtCollectionCodec<?> collectionCodec;

  public EntityDescriptor(Class<T> target, CaseFormat format, MtCollectionCodec<?> collectionCodec) {
    this.target = requireNonNull(target);
    this.format = requireNonNull(format);
    this.collectionCodec = collectionCodec;
    this.fields = new EntityMetadata(target).fieldIndex(this::setCase);
    this.idMetadata = new IdMetadata<>(target, fields, this);
    this.entityAnnotation = requireNonNull(target.getDeclaredAnnotation(MtEntity.class));
    this.fieldIndex = new HashMap<>();

    fields.values().forEach(fm -> fm.hasCollection().ifPresent(mc -> {
      fieldIndex.put(fm.field, fm);
      if (collectionCodec == null) {
        String msg = String.format("Collection field [%s] found, but no collection codec is assigned.", fm);
        throw new IllegalStateException(msg);
      } else if (!mc.sqlType().equalsIgnoreCase(collectionCodec.getTargetSqlType())) {
        String msg = String.format(
            "Collection codec with target SQL type [%s] does not support encoding for field of type [%s]",
            collectionCodec.getTargetSqlType(), mc.sqlType());
        throw new IllegalStateException(msg);
      }
    }));
  }

  public Collection<String> propertyNames(boolean includePrimaryKey) {
    Set<String> fNames = new LinkedHashSet<>(fields.keySet());
    if (!includePrimaryKey) { fNames.remove(idMetadata.getIdFieldName()); }
    return fNames;
  }

  public String propertyNamesCsv(boolean includePrimaryKey) {
    return String.join(COMMA_SPC, propertyNames(includePrimaryKey));
  }

  public String placeholderCsv(boolean includePrimaryKey) {
    return propertyNames(includePrimaryKey).stream()
        .map(k -> String.format(":%s", k)).collect(joining(COMMA_SPC));
  }

  public String placeHolderAssignmentCsv(boolean includePrimaryKey) {
    return propertyNames(includePrimaryKey).stream()
        .map(k -> String.format("%s = :%s", k, k))
        .collect(joining(COMMA_SPC));
  }

  public <K> K extract(T target, String property) {
    return (K) doExtract(target, getField(property));
  }

  public Map<String, Object> extractAll(T target, boolean includePrimaryKey) {
    Map<String, Object> vals = new LinkedHashMap<>();
    fields.forEach((fName, fl) -> vals.put(fName, extract(target, fName)));
    if (!includePrimaryKey) { vals.remove(idMetadata.getIdFieldName()); }
    return vals;
  }

  public Object [] extractPkComponents(T target) { return idMetadata.extractPkComponents(target); }

  public Field getField(String name) {
    requireNonNull(name);
    FieldMetadata fm = fields.get(name);
    if (fm == null) { throw new IllegalArgumentException(String.format("Attribute field [%s] not found", name)); }
    return fm.field;
  }

  public Object doExtract(T target, Field f) {
    try {
      if (Collection.class.isAssignableFrom(f.getType())) {
        Optional<Class<?>> collectionType = fieldIndex.get(f).getCollectionType();
        if (!collectionType.isPresent()) {
          String msg = String.format("Missing collection field metadata for field [%s]", f);
          throw new IllegalStateException(msg);
        }
        EntityCollection ec = new EntityCollection().with((Collection) f.get(target), collectionType.get());
        return collectionCodec.write(ec);
      }
      return f.get(target);
    }
    catch (Exception e) {
      throw new IllegalStateException("Object field extraction failed", e);
    }
  }

  private String setCase(FieldMetadata in) {
    switch (format) {
      case LOWER_CASE: return in.field.getName().toLowerCase();
      case UPPER_CASE: return in.field.getName().toUpperCase();
    }
    return in.field.getName();
  }

  public boolean isFixedPrimaryKey() { return entityAnnotation.fixedId(); }
  public MtCollectionCodec<?> getCollectionCodec() { return collectionCodec; }
  public Class<T> getTarget() { return target; }
  public CaseFormat getFormat() { return format; }
  public String getPrimaryKeyField() { return idMetadata.getIdFieldName(); }
  public Collection<FieldMetadata> getFields() { return fields.values(); }
}
