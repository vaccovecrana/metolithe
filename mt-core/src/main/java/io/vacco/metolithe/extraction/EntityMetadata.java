package io.vacco.metolithe.extraction;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class EntityMetadata {

  private final Class<?> target;
  private final Set<FieldMetadata> metadata;

  public EntityMetadata(Class<?> target) {
    this.target = requireNonNull(target);
    List<Class<?>> classes = new ArrayList<>();
    while (target != null) {
      classes.add(target);
      target = target.getSuperclass();
    }
    this.metadata = classes.stream().flatMap(cl -> Arrays.stream(cl.getDeclaredFields())
        .map(FieldMetadata::new)
        .filter(FieldMetadata::isEntityField))
        .peek(fm -> {
          if (fm.hasCollection().isPresent() && !fm.isValidCollectionField()) {
            String msg = String.format("Collection field [%s] cannot specify index or attribute constraints", fm.field);
            throw new IllegalStateException(msg);
          }
        }).collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public Map<String, FieldMetadata> fieldIndex(Function<FieldMetadata, String> keyNameFn) {
    return metadata.stream().collect(Collectors.toMap(keyNameFn, Function.identity()));
  }

  public Stream<FieldMetadata> rawFields() {
    return metadata.stream();
  }

  public Class<?> getTarget() { return target; }
}
