package io.vacco.metolithe.extraction;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class AnnotationExtractor {

  public static Stream<Annotation> apply(Field f) {
    return Arrays.stream(f.getDeclaredAnnotations()).flatMap(an0 -> {
      ArrayList<Annotation> fm = new ArrayList<>();
      fm.add(an0);
      fm.addAll(Arrays.asList(an0.annotationType().getDeclaredAnnotations()));
      return fm.stream();
    });
  }

  public static Annotation[] asArray(Field f) {
    return apply(f).toArray(Annotation[]::new);
  }
}
