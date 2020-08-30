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

  private static final Class<?>[] rt = new Class[] {
      Retention.class, Target.class
  };

  private static final Class<?>[] mta = new Class[] {
      MtCompIndex.class, MtEntity.class, MtField.class,
      MtFk.class, MtIndex.class, MtNotNull.class,
      MtPk.class, MtUnique.class, MtVarchar.class
  };

  private final Field f;
  private final Set<Annotation> annotations;

  private boolean inSet(Annotation a, Class<?>[] annotations) {
    Class<? extends Annotation> ac0 = a.getClass();
    for (Class<?> ac : annotations) {
      if (ac.isAssignableFrom(ac0)) {
        return true;
      }
    }
    return false;
  }

  private boolean isMtAnnotation(Annotation a) { return inSet(a, mta); }
  private boolean isRtAnnotation(Annotation a) { return inSet(a, rt); }

  private Stream<Annotation> scan(Annotation a) {
    if (isRtAnnotation(a)) {
      return empty();
    }
    return concat(
        of(a), stream(a.annotationType().getAnnotations()).flatMap(this::scan)
    );
  }

  public MtFieldDescriptor(Field f) {
    this.f = requireNonNull(f);
    this.annotations = stream(f.getAnnotations())
        .flatMap(this::scan)
        .filter(this::isMtAnnotation)
        .collect(Collectors.toSet());
  }

  @Override public int hashCode() { return this.f.hashCode(); }

  @Override public String toString() {
    return String.format("%s=[%s]",
        f.getName(),
        annotations.stream()
            .map(a -> a.annotationType().getSimpleName())
            .collect(Collectors.joining(", "))
    );
  }
}
