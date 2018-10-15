package io.vacco.metolithe.core;

import io.vacco.metolithe.annotations.MtId;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.*;
import static java.util.stream.Collectors.joining;
import static io.vacco.metolithe.extraction.FieldFilter.*;

public class EntityDescriptor<T> {

  public enum CaseFormat { UPPER_CASE, LOWER_CASE, KEEP_CASE }
  public static final String COMMA_SPC = ", ";

  private final Class<?> target;
  private final Map<String, Field> fieldMap;
  private final List<Field> pkFields;
  private final String pkFieldName;
  private final CaseFormat format;

  public EntityDescriptor(Class<T> target, CaseFormat format) {
    this.target = requireNonNull(target);
    this.format = requireNonNull(format);
    Map<String, List<Field>> rawFields = scanClassFields(target);
    this.fieldMap = assignFields(rawFields);
    this.pkFields = assignPkComponents(rawFields);
    this.pkFieldName = getSeedPkComponent();
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
    return pkFields.stream().map(fl -> doExtract(target, fl)).toArray(Object[]::new);
  }

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

  private List<Field> assignPkComponents(Map<String, List<Field>> rawAttributes) {
    Map<Integer, Field> pkFields = new HashMap<>();
    rawAttributes.forEach((key, value) -> value.forEach(fl0 -> {
      Optional<MtId> ownPk = hasOwnPrimaryKey(target, fl0);
      ownPk.ifPresent(mtId -> {
        if (pkFields.containsKey(mtId.position())) {
          throw new IllegalStateException(String.format(
              "%s contains duplicate primary key field positions: [%s].", target, pkFields));
        }
        pkFields.put(mtId.position(), fl0);
      });
    }));
    return pkFields.entrySet().stream()
        .sorted(Comparator.comparingInt(Map.Entry::getKey))
        .map(Map.Entry::getValue).collect(Collectors.toList());
  }

  private String getSeedPkComponent() {
    Optional<String> opk = fieldMap.entrySet().stream().filter(e0 -> {
      Optional<MtId> oid = hasOwnPrimaryKey(target, e0.getValue());
      return oid.isPresent() && oid.get().position() == 0;
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
      case KEEP_CASE: return in;
      case LOWER_CASE: return in.toLowerCase();
      case UPPER_CASE: return in.toUpperCase();
      default: throw new IllegalStateException("Unable to determine property case formatting.");
    }
  }
}
