package io.vacco.metolithe.core;

import io.vacco.metolithe.annotations.*;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static java.lang.String.*;
import static java.lang.reflect.Modifier.*;

public class MtDescriptor<T> {

  private static final Object[] empty = new Object[]{};

  private final Class<T> cl;
  private final MtCaseFormat fmt;

  private final List<MtFieldDescriptor> fields;
  private final List<MtFieldDescriptor> fieldsNoPk;
  private final MtFieldDescriptor pkField;

  public MtDescriptor(Class<T> entity, MtCaseFormat fmt) {
    this.cl = Objects.requireNonNull(entity);
    this.fmt = Objects.requireNonNull(fmt);
    this.fields = Arrays.stream(entity.getFields())
      .filter(f -> isPublic(f.getModifiers()) && !isStatic(f.getModifiers()))
      .map(f -> new MtFieldDescriptor(f, fmt)).collect(toList());
    this.fieldsNoPk = this.fields.stream().filter(fd -> !fd.isPk()).collect(toList());
    List<MtFieldDescriptor> pkds = this.fields.stream().filter(MtFieldDescriptor::isPk).collect(toList());
    if (pkds.size() > 1) {
      throw new MtException.MtMultiplePkDefinitionsException(pkds);
    }
    this.pkField = pkds.isEmpty() ? null : pkds.get(0);
  }

  @SuppressWarnings("unchecked")
  public <E extends Enum<?>> List<Class<E>> getEnumFields() {
    return fields.stream()
      .filter(fd -> Enum.class.isAssignableFrom(fd.getType()))
      .map(fd -> (Class<E>) fd.getType())
      .collect(toList());
  }

  public Stream<MtFieldDescriptor> get(Class<? extends Annotation> target) {
    return fields.stream().filter(fd -> fd.get(target).isPresent());
  }

  public Map<String, List<MtFieldDescriptor>> getCompositeIndexes() {
    return fieldsNoPk.stream()
      .filter(fd -> fd.get(MtCompIndex.class).isPresent())
      .collect(groupingBy(fd -> fd.get(MtCompIndex.class).get().name()));
  }

  public Object[] getPkValues(T t) {
    if (t == null || this.pkField == null) {
      return empty;
    }
    Map<Integer, Object> pkValues = new TreeMap<>();
    for (MtFieldDescriptor fd : fields) {
      Optional<MtUnique> ou = fd.get(MtUnique.class);
      if (ou.isPresent() && ou.get().inPk()) {
        Object comp = fd.getValue(t);
        if (comp == null) throw new MtException.MtMissingPkComponentException(t, fd);
        pkValues.put(ou.get().idx(), comp);
      }
    }
    return pkValues.values().toArray();
  }

  public Map<String, Object> getAll(T t) {
    Map<String, Object> comps = new LinkedHashMap<>();
    for (MtFieldDescriptor fd : fields) {
      comps.put(fd.getFieldName(), fd.getValue(t));
    }
    return comps;
  }

  public boolean matches(Class<?> other) {
    return cl == other;
  }

  public List<MtFieldDescriptor> getFields(boolean withPk) {
    return withPk ? fields : fieldsNoPk;
  }

  public Optional<MtFieldDescriptor> getPkField() {
    return pkField != null ? Optional.of(pkField) : Optional.empty();
  }

  public MtFieldDescriptor getField(String name) {
    return fields.stream()
      .filter(fd -> fd.getFieldName().equalsIgnoreCase(name)).findFirst()
      .orElseThrow(() -> new MtException.MtMissingFieldException(name, this));
  }

  public Class<T> getType() {
    return cl;
  }

  public String getClassName() {
    return cl.getCanonicalName();
  }

  public String getName() {
    return fmt.of(cl.getSimpleName());
  }

  public MtCaseFormat getFormat() {
    return fmt;
  }

  @Override
  public String toString() {
    return format("<%s>%s", cl.getSimpleName(), fields);
  }
}
