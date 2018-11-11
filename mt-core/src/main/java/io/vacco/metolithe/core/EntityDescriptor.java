package io.vacco.metolithe.core;

import io.vacco.metolithe.annotations.*;
import io.vacco.metolithe.util.TypeUtil;
import java.lang.reflect.Field;
import java.util.*;

import static java.util.Objects.*;
import static java.util.stream.Collectors.joining;
import static io.vacco.metolithe.extraction.FieldFilter.*;

public class EntityDescriptor<T> {

  public enum CaseFormat { UPPER_CASE, LOWER_CASE, KEEP_CASE }
  public static final String COMMA_SPC = ", ";

  private final Class<?> target;
  private final Map<String, Field> fieldMap;
  private final Map<Integer, Map<Integer, Field>> pkFieldGroups;
  private final String pkFieldName;
  private final CaseFormat format;
  private final MtEntity entityAnnotation;

  public EntityDescriptor(Class<T> target, CaseFormat format) {
    this.target = requireNonNull(target);
    this.format = requireNonNull(format);
    Map<String, List<Field>> rawFields = scanClassFields(target);
    this.fieldMap = assignFields(rawFields);
    this.pkFieldName = getSeedPkComponent();
    this.pkFieldGroups = assignPkComponents(rawFields);
    this.entityAnnotation = requireNonNull(target.getDeclaredAnnotation(MtEntity.class));
    fieldMap.values().forEach(fl0 -> fl0.setAccessible(true));
  }

  public Collection<String> propertyNames(boolean includePrimaryKey) {
    Set<String> fNames = new LinkedHashSet<>(fieldMap.keySet());
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
    return (K) doExtract(target, fieldMap.get(property));
  }

  public Map<String, Object> extractAll(T target, boolean includePrimaryKey) {
    Map<String, Object> vals = new LinkedHashMap<>();
    fieldMap.forEach((fName, fl) -> vals.put(fName, extract(target, fName)));
    if (!includePrimaryKey) { vals.remove(pkFieldName); }
    return vals;
  }

  public Object [] extractPkComponents(T target) {
    Optional<Object []> components = pkFieldGroups.values().stream()
        .map(fMap -> fMap.values().stream().map(fl -> doExtract(target, fl)).toArray(Object[]::new))
        .filter(TypeUtil::allNonNull)
        .findFirst();
    if (!components.isPresent()) {
      String msg = String.format("No non-null primary key component value set available for [%s]", target);
      throw new IllegalStateException(msg);
    }
    return components.get();
  }

  public boolean isFixedPrimaryKey() { return entityAnnotation.fixedId(); }
  public Class<?> getTarget() { return target; }
  public CaseFormat getFormat() { return format; }
  public Collection<Field> getAllFields() { return fieldMap.values(); }
  public String getPrimaryKeyField() { return pkFieldName; }
  public Field getField(String name) {
    requireNonNull(name);
    Field f = fieldMap.get(name);
    if (f == null) { throw new IllegalArgumentException(String.format("Attribute field [%s] not found", name)); }
    return f;
  }

  private Map<String, List<Field>> scanClassFields(Class<?> cl0) {
    Map<String, List<Field>> all = new LinkedHashMap<>();
    while (cl0 != null) {
      Arrays.asList(cl0.getDeclaredFields()).forEach(fl0 -> {
        List<Field> mappings = all.computeIfAbsent(setCase(fl0.getName()), k -> new ArrayList<>());
        boolean hasPk = hasPrimaryKey(fl0).isPresent();
        boolean hasIndex = hasIndex(fl0).isPresent();
        boolean hasAttribute = hasAttribute(fl0).isPresent();
        if (hasPk || hasIndex || hasAttribute) { mappings.add(fl0); }
      });
      cl0 = cl0.getSuperclass();
    }
    return all;
  }

  private Map<String, Field> assignFields(Map<String, List<Field>> rawAttributes) {
    Map<String, Field> result = new LinkedHashMap<>();
    rawAttributes.forEach((key, value) -> {
      if (!value.isEmpty()) {
        Optional<Field> fTarget = value.stream()
            .filter(fl0 -> {
              boolean isPk = isOwnPrimaryKey(target, fl0);
              boolean isIndex = isOwnIndex(target, fl0);
              boolean hasAttribute = hasAttribute(fl0).isPresent();
              return isPk || isIndex || hasAttribute;
            }).findFirst();
        fTarget.ifPresent(field -> result.put(key, field));
      }
    });
    return result;
  }

  private Map<Integer, Map<Integer, Field>> assignPkComponents(Map<String, List<Field>> rawAttributes) {
    Map<Integer, Map<Integer, Field>> pkFields = new TreeMap<>();
    rawAttributes.forEach((key, value) -> value.forEach(fl0 -> {
      Optional<MtIdGroup> pkComponent = hasIdGroup(fl0);
      pkComponent.ifPresent(mtGrp -> {
        Map<Integer, Field> groupMap = pkFields.computeIfAbsent(mtGrp.number(), group -> new TreeMap<>());
        if (!groupMap.containsKey(mtGrp.position())) {
          groupMap.put(mtGrp.position(), fl0);
        } else {
          String msg = String.format(
              String.join("\n",
                  "[%s] contains duplicate primary key field group positions:",
                  "field: [%s], group: [%s], position: [%s]",
                  "Specify a unique position value for each primary key field group."
              ), target, fl0, mtGrp.number(), mtGrp.position());
          throw new IllegalArgumentException(msg);
        }
      });
    }));
    if (pkFields.isEmpty()) {
      throw new IllegalStateException(String.format("No field group definitions were found for class [%s].", target));
    }
    return pkFields;
  }

  private String getSeedPkComponent() {
    Optional<String> opk = fieldMap.entrySet().stream().filter(e0 -> {
      Optional<MtId> oid = hasOwnPrimaryKey(target, e0.getValue());
      return oid.isPresent();
    }).map(Map.Entry::getKey).findFirst();
    if (!opk.isPresent()) {
      throw new IllegalStateException(String.format("%s does not define a primary key (MtId) field.", target));
    }
    return opk.get();
  }

  private Object doExtract(T target, Field f) {
    try { return f.get(target); }
    catch (Exception e) {
      throw new IllegalStateException("Object field extraction failed", e);
    }
  }

  private String setCase(String in) {
    switch (format) {
      case LOWER_CASE: return in.toLowerCase();
      case UPPER_CASE: return in.toUpperCase();
    }
    return in;
  }
}
