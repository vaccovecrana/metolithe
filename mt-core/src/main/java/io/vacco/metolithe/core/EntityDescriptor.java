package io.vacco.metolithe.core;

import io.vacco.metolithe.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.*;
import static java.util.stream.Collectors.joining;

public class EntityDescriptor<T> {

  private static final String COMMA_SPC = ", ";

  private final Class<?> target;
  private final Set<Field> entityFields;
  private final Set<Field> allFields;
  private final Map<String, Field> nameToField = new HashMap<>();
  private final Field primaryKeyField;

  public EntityDescriptor(Class<T> target) {
    this.target = requireNonNull(target);
    Class<?> cl0 = target;
    List<Field> fields = new ArrayList<>();
    while (cl0 != null) {
      fields.addAll(Arrays.asList(cl0.getDeclaredFields()));
      cl0 = cl0.getSuperclass();
    }
    entityFields = fields.stream().filter(this::isEntityField).collect(Collectors.toCollection(LinkedHashSet::new));
    entityFields.forEach(fl0 -> nameToField.put(fl0.getName(), fl0));

    Optional<Field> opk = fields.stream().filter(this::isPrimaryKeyField).findFirst();
    if (!opk.isPresent()) {
      String msg = String.format("%s does not define a primary key (MtId) field.", target);
      throw new IllegalStateException(msg);
    }
    primaryKeyField = opk.get();
    entityFields.forEach(fl0 -> fl0.setAccessible(true));
    primaryKeyField.setAccessible(true);
    nameToField.put(primaryKeyField.getName(), primaryKeyField);
    allFields = new LinkedHashSet<>();
    allFields.add(primaryKeyField);
    allFields.addAll(entityFields);
  }

  public Collection<String> propertyNames(boolean includePrimaryKey) {
    if (includePrimaryKey) { return allFields.stream().map(Field::getName).collect(Collectors.toSet()); }
    return entityFields.stream().map(Field::getName).collect(Collectors.toSet());
  }

  public String propertyNamesCsv(boolean includePrimaryKey) {
    return propertyNames(includePrimaryKey).stream().collect(joining(COMMA_SPC));
  }

  public String placeholderCsv(boolean includePrimaryKey) {
    return propertyNames(includePrimaryKey).stream()
        .map(k -> String.format(":%s", k))
        .collect(joining(COMMA_SPC));
  }

  public String placeHolderAssignmentCsv(boolean includePrimaryKey) {
    return propertyNames(includePrimaryKey).stream()
        .map(k -> String.format("%s = :%s", k, k))
        .collect(joining(COMMA_SPC));
  }

  public <K> K extract(T target, String property) {
    return (K) doExtract(target, nameToField.get(property));
  }

  public Map<String, Object> extractAll(T target, Function<Object, Object> postProcessor,
                                        boolean includePrimaryKey) {
    Map<String, Object> props = new HashMap<>();
    entityFields.forEach(fl0 -> props.put(fl0.getName(),
        postProcessor.apply(extract(target, fl0.getName()))));
    if (includePrimaryKey) {
      props.put(primaryKeyField.getName(),
          postProcessor.apply(extract(target, primaryKeyField.getName())));
    }
    return props;
  }

  private Object doExtract(T target, Field f) {
    try { return f.get(target); }
    catch (IllegalAccessException e) {
      throw new IllegalStateException("Object field extraction failed", e);
    }
  }

  public Set<Field> getAllFields() { return allFields; }
  public Field getPrimaryKeyField() { return primaryKeyField; }
  public Field getField(String name) {
    requireNonNull(name);
    Field f = nameToField.get(name);
    if (f == null) {
      throw new IllegalArgumentException(String.format("Attribute field [%s] not found", name));
    }
    return f;
  }

  public Class<?> getTarget() { return target; }

  private boolean isEntityField(Field f) {
    requireNonNull(f);
    return Arrays.stream(f.getDeclaredAnnotations())
        .anyMatch(an0 -> an0.annotationType() == MtIndex.class
            || an0.annotationType() == MtAttribute.class);
  }

  private boolean isPrimaryKeyField(Field f) {
    requireNonNull(f);
    return Arrays.stream(f.getDeclaredAnnotations())
        .anyMatch(an0 -> an0.annotationType() == MtId.class);
  }
}
