package io.vacco.metolithe.core;

import io.vacco.metolithe.annotations.MtUnique;

import java.lang.reflect.Field;
import java.util.*;

import static java.util.stream.Collectors.*;
import static java.lang.String.*;
import static java.lang.reflect.Modifier.*;

public class MtDescriptor<T> {

  private static final Object[] empty = new Object[] {};

  private final Class<T> target;
  private final List<MtFieldDescriptor> fields;
  private final List<MtFieldDescriptor> fieldsNoPk;
  private final MtFieldDescriptor pkField;

  public MtDescriptor(Class<T> entity) {
    this.target = Objects.requireNonNull(entity);
    this.fields = Arrays.stream(entity.getFields())
        .filter(f -> isPublic(f.getModifiers()) && !isStatic(f.getModifiers()))
        .map(MtFieldDescriptor::new).collect(toList());
    this.fieldsNoPk = this.fields.stream().filter(fd -> !fd.isPk()).collect(toList());
    List<MtFieldDescriptor> pkds = this.fields.stream().filter(MtFieldDescriptor::isPk).collect(toList());
    if (pkds.size() > 1) {
      throw new IllegalArgumentException(format(
          "%s defines multiple primary key fields: %s", target.getCanonicalName(), pkds
      ));
    }
    this.pkField = pkds.isEmpty() ? null : pkds.get(0);
  }

  public <E extends Enum<?>> List<Class<E>> getEnumFields() {
    return fields.stream()
        .filter(fd -> Enum.class.isAssignableFrom(fd.getField().getType()))
        .map(fd -> (Class<E>) fd.getField().getType())
        .collect(toList());
  }

  private Object doGet(Field f, T t) {
    try {
      return f.get(t);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  public Object[] getPkValues(T t) {
    if (t == null || this.pkField == null) return empty;
    Map<Integer, Object> pkValues = new TreeMap<>();
    for (MtFieldDescriptor fd : fields) {
      Optional<MtUnique> ou = fd.get(MtUnique.class);
      if (ou.isPresent() && ou.get().inPk()) {
        Object comp = doGet(fd.getField(), t);
        if (comp == null) throw new IllegalArgumentException(format(
            "Missing primary key component for [%s] on [%s]",
            t.getClass().getCanonicalName(), fd
        ));
        pkValues.put(ou.get().idx(), comp);
      }
    }
    return pkValues.values().toArray();
  }

  public Map<MtFieldDescriptor, Object> getAll(T t) {
    Map<MtFieldDescriptor, Object> comps = new LinkedHashMap<>();
    for (MtFieldDescriptor fd : fields) {
      comps.put(fd, doGet(fd.getField(), t));
    }
    return comps;
  }

  public List<MtFieldDescriptor> getFields(boolean withPk) {
    return withPk ? fields : fieldsNoPk;
  }

  public Class<T> getTarget() { return target; }

  @Override
  public String toString() {
    return format("<%s>%s", target.getSimpleName(), fields);
  }
}
