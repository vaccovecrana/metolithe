package io.vacco.metolithe.core;

import io.vacco.metolithe.annotations.*;
import io.vacco.metolithe.extraction.EntityMetadata;
import io.vacco.metolithe.extraction.FieldMetadata;
import io.vacco.metolithe.spi.MtCollectionCodec;
import io.vacco.metolithe.util.TypeUtil;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.*;
import static java.util.stream.Collectors.joining;

public class EntityDescriptor<T> {

  public enum CaseFormat { UPPER_CASE, LOWER_CASE, KEEP_CASE }
  public static final String COMMA_SPC = ", ";

  private final Class<T> target;
  private final Map<String, FieldMetadata> fields;
  private final String pkFieldName;
  private final Map<Integer, Map<Integer, FieldMetadata>> pkFieldGroups;
  private final CaseFormat format;
  private final MtEntity entityAnnotation;
  private MtCollectionCodec<?> collectionCodec;

  public EntityDescriptor(Class<T> target, CaseFormat format, MtCollectionCodec<?> collectionCodec) {
    this.target = requireNonNull(target);
    this.format = requireNonNull(format);
    this.collectionCodec = collectionCodec;

    EntityMetadata em = new EntityMetadata(target);
    this.fields = em.fieldIndex(this::setCase);
    this.pkFieldName = getSeedPkComponent();
    this.pkFieldGroups = assignPkComponents();
    this.entityAnnotation = requireNonNull(target.getDeclaredAnnotation(MtEntity.class));

    em.rawFields().forEach(fm -> fm.hasCollection().ifPresent(mc -> {
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
    if (!includePrimaryKey) { fNames.remove(pkFieldName); }
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
    if (!includePrimaryKey) { vals.remove(pkFieldName); }
    return vals;
  }

  public Object [] extractPkComponents(T target) {
    if (pkFieldGroups.isEmpty()) {
      String msg = String.join("\n",
          "Entity [%s] does not define primary key attribute groups.",
          "Either set the primary key value externally or define id attribute groups in your entity.");
      throw new IllegalStateException(String.format(msg, target));
    }
    Optional<Object []> components = pkFieldGroups.values().stream()
        .map(fMap -> fMap.values().stream().map(fl -> doExtract(target, fl.field)).toArray(Object[]::new))
        .filter(TypeUtil::allNonNull)
        .findFirst();
    if (!components.isPresent()) {
      String msg = String.format("No non-null primary key component attribute set available for [%s]", target);
      throw new IllegalStateException(msg);
    }
    return components.get();
  }

  public boolean isFixedPrimaryKey() { return entityAnnotation.fixedId(); }
  public MtCollectionCodec<?> getCollectionCodec() { return collectionCodec; }
  public Class<T> getTarget() { return target; }
  public CaseFormat getFormat() { return format; }
  public String getPrimaryKeyField() { return pkFieldName; }
  public Field getField(String name) {
    requireNonNull(name);
    FieldMetadata fm = fields.get(name);
    if (fm == null) { throw new IllegalArgumentException(String.format("Attribute field [%s] not found", name)); }
    return fm.field;
  }

  private Map<Integer, Map<Integer, FieldMetadata>> assignPkComponents() {
    Map<Integer, Map<Integer, FieldMetadata>> pkFields = new TreeMap<>();
    fields.values().forEach(fm -> fm.hasIdGroup().ifPresent(mtGrp -> {
      Map<Integer, FieldMetadata> groupMap = pkFields.computeIfAbsent(mtGrp.number(), group -> new TreeMap<>());
      if (!groupMap.containsKey(mtGrp.position())) {
        groupMap.put(mtGrp.position(), fm);
      } else {
        String msg = String.format(
            String.join("\n",
                "[%s] contains duplicate primary key field group positions:",
                "field: [%s], group: [%s], position: [%s]",
                "Specify a unique position value for each primary key field group."
            ), target, fm, mtGrp.number(), mtGrp.position());
        throw new IllegalArgumentException(msg);
      }
    }));
    return pkFields;
  }

  private String getSeedPkComponent() {
    List<String> opk = fields.entrySet().stream()
        .filter(e0 -> e0.getValue().hasPrimaryKeyOf(target).isPresent())
        .map(Map.Entry::getKey).collect(Collectors.toList());
    if (opk.isEmpty()) { throw new IllegalStateException(String.format("%s does not define a primary key (MtId) field.", target)); }
    if (opk.size() > 1) {
      throw new IllegalStateException(String.format("Multiple primary key (MtId) field definitions found, specify only one: [%s]", opk));
    }
    return opk.get(0);
  }

  private Object doExtract(T target, Field f) {
    try {
      if (Collection.class.isAssignableFrom(f.getType())) {
        return collectionCodec.write((Collection) f.get(target));
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
}
