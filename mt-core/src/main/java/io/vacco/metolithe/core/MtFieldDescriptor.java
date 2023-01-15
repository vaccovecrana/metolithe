package io.vacco.metolithe.core;

import io.vacco.metolithe.annotations.*;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.*;

import static java.util.Objects.*;
import static java.util.stream.Stream.*;
import static java.util.Arrays.*;

public class MtFieldDescriptor {

  private static final List<Class<? extends Annotation>> rt = asList(Retention.class, Target.class);
  private static final List<Class<? extends Annotation>> mta = asList(
    MtEntity.class,
    MtPk.class, MtFk.class,
    MtField.class, MtVarchar.class, MtNotNull.class,
    MtCompIndex.class, MtIndex.class, MtUnique.class
  );

  private final Field f;
  private final List<Annotation> annotations;
  private final MtCaseFormat fmt;

  private boolean match(Class<? extends Annotation> to, Class<? extends Annotation> from) {
    return to.isAssignableFrom(from);
  }

  private boolean inSet(Class<? extends Annotation> ac0, List<Class<? extends Annotation>> acl) {
    return acl.stream().anyMatch(ac -> match(ac, ac0));
  }

  private Stream<Annotation> scan(Annotation a) {
    if (inSet(a.annotationType(), rt)) {
      return empty();
    }
    return concat(of(a), stream(a.annotationType().getAnnotations()).flatMap(this::scan));
  }

  public MtFieldDescriptor(Field f, MtCaseFormat fmt) {
    this.f = requireNonNull(f);
    this.fmt = requireNonNull(fmt);
    this.annotations = stream(f.getAnnotations())
      .flatMap(this::scan)
      .filter(a -> inSet(a.annotationType(), mta))
      .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  public <T extends Annotation> Optional<T> get(Class<T> annotation) {
    return annotations.stream()
      .filter(an0 -> match(annotation, an0.annotationType()))
      .map(an0 -> (T) an0).findFirst();
  }

  public boolean isPk() {
    return get(MtPk.class).isPresent();
  }

  public MtCaseFormat getFormat() {
    return fmt;
  }

  public String getFieldClassName() {
    return this.f.getType().getCanonicalName();
  }

  public String getFieldName() {
    return fmt.of(this.f.getName());
  }

  public String getFieldRawName() {
    return this.f.getName();
  }

  public Class<?> getType() {
    return f.getType();
  }

  @SuppressWarnings("unchecked")
  public <V> V getValue(Object o) {
    try {
      return (V) f.get(o);
    } catch (Exception e) {
      throw new MtException.MtFieldAccessException(o, null, e);
    }
  }

  public <T> void setValue(T o, Object val) {
    try {
      f.set(o, val);
    } catch (IllegalAccessException e) {
      throw new MtException.MtFieldAccessException(o, val, e);
    }
  }

  @Override public String toString() {
    String ants = annotations.stream()
      .map(a -> a.annotationType().getSimpleName())
      .collect(Collectors.joining(", "));
    return String.format("%s=[%s]", f.getName(), ants);
  }
}
