package io.vacco.metolithe.extraction;

import io.vacco.metolithe.annotations.MtId;
import io.vacco.metolithe.util.TypeUtil;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class IdMetadata<T> {

  private final String idFieldName;
  private final Map<Integer, Map<Integer, FieldMetadata>> pkFieldGroups;
  private final FieldExtractor<T> fieldExtractor;

  public IdMetadata(Class<T> root, Map<String, FieldMetadata> fieldIndex, FieldExtractor<T> fieldExtractor) {
    this.fieldExtractor = Objects.requireNonNull(fieldExtractor);
    List<Map.Entry<String, FieldMetadata>> storageTargetFields = fieldIndex.entrySet().stream()
        .filter(e -> e.getValue().hasPrimaryKeyOf(root).map(MtId::groupTarget).orElse(false))
        .collect(Collectors.toList());
    if (storageTargetFields.size() != 1) {
      String err = format("Class [%s] is missing, or defines more than one primary key storage field: %s", root, storageTargetFields);
      throw new IllegalStateException(err);
    }
    this.idFieldName = storageTargetFields.get(0).getKey();

    Map<Integer, Map<Integer, FieldMetadata>> pkGroups = new TreeMap<>();
    fieldIndex.values().forEach(fm -> fm.hasIdGroup().ifPresent(mtGrp -> {
      Map<Integer, FieldMetadata> groupMap = pkGroups.computeIfAbsent(mtGrp.number(), group -> new TreeMap<>());
      if (!groupMap.containsKey(mtGrp.position())) {
        groupMap.put(mtGrp.position(), fm);
      } else {
        String msg = String.format(
            String.join("\n",
                "[%s] contains duplicate primary key field group positions:",
                "field: [%s], group: [%s], position: [%s]",
                "Specify a unique position value for each primary key field group."
            ), root, fm, mtGrp.number(), mtGrp.position());
        throw new IllegalArgumentException(msg);
      }
    }));
    this.pkFieldGroups = pkGroups;
  }

  public Object [] extractPkComponents(T target) {
    if (pkFieldGroups.isEmpty()) {
      String msg = String.join("\n",
          "Entity [%s] does not define primary key attribute groups.",
          "Either set the primary key value externally or define id attribute groups in your entity.");
      throw new IllegalStateException(String.format(msg, target));
    }
    Optional<Object []> components = pkFieldGroups.values().stream()
        .map(fMap -> fMap.values().stream().map(fl -> fieldExtractor.doExtract(target, fl.field)).toArray(Object[]::new))
        .filter(TypeUtil::allNonNull)
        .findFirst();
    if (!components.isPresent()) {
      String msg = String.format("No non-null primary key component attribute set available for [%s]", target);
      throw new IllegalStateException(msg);
    }
    return components.get();
  }

  public String getIdFieldName() { return idFieldName; }
}
