package io.vacco.metolithe.codegen.liquibase;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.*;
import io.vacco.metolithe.annotations.*;
import io.vacco.metolithe.extraction.EntityMetadata;
import java.util.*;

import static java.util.Objects.*;

public class EntityExtractor implements ClassAnnotationMatchProcessor {

  private final Set<EntityMetadata> entityClasses = new HashSet<>();

  public Collection<EntityMetadata> apply(String ... packageSpecs) {
    requireNonNull(packageSpecs);
    if (packageSpecs.length == 0) throw new IllegalArgumentException();
    new FastClasspathScanner(packageSpecs)
        .ignoreFieldVisibility()
        .matchClassesWithAnnotation(MtEntity.class, this)
        .scan();
    return entityClasses;
  }

  @Override public void processMatch(Class<?> classWithAnnotation) {
    entityClasses.add(new EntityMetadata(classWithAnnotation));
  }
}
