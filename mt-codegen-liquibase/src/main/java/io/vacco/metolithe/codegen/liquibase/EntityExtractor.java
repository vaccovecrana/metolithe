package io.vacco.metolithe.codegen.liquibase;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.*;
import io.vacco.metolithe.annotations.*;
import io.vacco.metolithe.core.EntityDescriptor;
import io.vacco.metolithe.spi.CollectionCodec;

import java.util.*;

import static java.util.Objects.*;

public class EntityExtractor implements ClassAnnotationMatchProcessor {

  private final Set<Class<?>> entityClasses = new HashSet<>();

  public Collection<EntityDescriptor<?>> apply(EntityDescriptor.CaseFormat format,
                                               CollectionCodec<?> collectionCodec,
                                               String ... packageSpecs) {
    requireNonNull(packageSpecs);
    if (packageSpecs.length == 0) throw new IllegalArgumentException();
    new FastClasspathScanner(packageSpecs)
        .ignoreFieldVisibility()
        .matchClassesWithAnnotation(MtEntity.class, this).scan();
    Set<EntityDescriptor<?>> result = new HashSet<>();
    entityClasses.forEach(cl0 -> result.add(new EntityDescriptor<>(cl0, format, collectionCodec)));
    return result;
  }

  @Override
  public void processMatch(Class<?> classWithAnnotation) {
    entityClasses.add(classWithAnnotation);
  }
}
