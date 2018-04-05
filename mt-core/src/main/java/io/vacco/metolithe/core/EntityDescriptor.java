package io.vacco.metolithe.core;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

import static java.util.Objects.*;
import static java.util.stream.Collectors.joining;
import static io.vacco.metolithe.core.FieldFilter.*;

public class EntityDescriptor<T> {

  public enum CaseFormat { UPPER_CASE, LOWER_CASE, KEEP_CASE }

  private static final String COMMA_SPC = ", ";
  private final Class<?> target;
  private final Map<String, Field> fieldMap = new LinkedHashMap<>();
  private final String pkFieldName;
  private final CaseFormat format;

  public EntityDescriptor(Class<T> target, CaseFormat format) {
    this.target = requireNonNull(target);
    this.format = requireNonNull(format);
    Class<?> cl0 = target;
    Map<String, List<Field>> all = new LinkedHashMap<>();
    while (cl0 != null) {
      Arrays.asList(cl0.getDeclaredFields()).forEach(fl0 -> {
        List<Field> mappings = all.get(fl0.getName());
        if (mappings == null) {
          mappings = new ArrayList<>();
          all.put(fl0.getName(), mappings);
        }
        if (hasPrimaryKey(fl0).isPresent() || hasIndex(fl0).isPresent() || hasAttribute(fl0).isPresent()) {
          mappings.add(fl0);
        }
      });
      cl0 = cl0.getSuperclass();
    }
    all.forEach((key, value) -> {
      if (!value.isEmpty()) {
        Optional<Field> fTarget = value.stream()
            .filter(fl0 -> isOwnId(target, fl0) || isOwnIndex(target, fl0) || hasAttribute(fl0).isPresent())
            .findFirst();
        fieldMap.put(setCase(key), fTarget.get());
      }
    });
    Optional<Field> opk = fieldMap.values().stream().filter(fl0 -> isOwnId(target, fl0)).findFirst();
    if (!opk.isPresent()) {
      String msg = String.format("%s does not define a primary key (MtId) field.", target);
      throw new IllegalStateException(msg);
    }
    pkFieldName = opk.get().getName();
    fieldMap.values().forEach(fl0 -> fl0.setAccessible(true));
  }

  public Collection<String> propertyNames(boolean includePrimaryKey) {
    Set<String> fNames = new LinkedHashSet<>(fieldMap.keySet());
    if (!includePrimaryKey) { fNames.remove(pkFieldName); }
    return fNames;
  }

  public String propertyNamesCsv(boolean includePrimaryKey) {
    return propertyNames(includePrimaryKey).stream()
        .map(this::setCase)
        .collect(joining(COMMA_SPC));
  }

  public String placeholderCsv(boolean includePrimaryKey) {
    return propertyNames(includePrimaryKey).stream()
        .map(k -> String.format(":%s", k))
        .map(this::setCase)
        .collect(joining(COMMA_SPC));
  }

  public String placeHolderAssignmentCsv(boolean includePrimaryKey) {
    return propertyNames(includePrimaryKey).stream()
        .map(k -> String.format("%s = :%s", k, k))
        .collect(joining(COMMA_SPC));
  }

  public <K> K extract(T target, String property) {
    return (K) doExtract(target, fieldMap.get(property));
  }

  public Map<String, Object> extractAll(T target, Function<Object, Object> postProcessor,
                                        boolean includePrimaryKey) {
    Map<String, Object> vals = new LinkedHashMap<>();
    fieldMap.forEach((fName, fl) -> vals.put(fName, postProcessor.apply(extract(target, fName))));
    if (!includePrimaryKey) { vals.remove(pkFieldName); }
    return vals;
  }

  private Object doExtract(T target, Field f) {
    try { return f.get(target); }
    catch (Exception e) {
      throw new IllegalStateException("Object field extraction failed", e);
    }
  }

  public Collection<Field> getAllFields() { return fieldMap.values(); }
  public String getPrimaryKeyField() { return setCase(pkFieldName); }
  public Field getField(String name) {
    requireNonNull(name);
    Field f = fieldMap.get(name);
    if (f == null) { throw new IllegalArgumentException(String.format("Attribute field [%s] not found", name)); }
    return f;
  }

  public Class<?> getTarget() { return target; }
  public CaseFormat getFormat() { return format; }

  private String setCase(String in) {
    switch (format) {
      case KEEP_CASE: return in;
      case LOWER_CASE: return in.toLowerCase();
      case UPPER_CASE: return in.toUpperCase();
      default: throw new IllegalStateException("Unable to determine property case formatting.");
    }
  }
}
