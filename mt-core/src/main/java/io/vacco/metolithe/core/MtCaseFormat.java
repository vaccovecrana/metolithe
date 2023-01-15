package io.vacco.metolithe.core;

import java.util.List;

import static java.util.stream.Collectors.*;
import static java.lang.String.*;

public enum MtCaseFormat {

  UPPER_CASE, LOWER_CASE, KEEP_CASE;

  public String of(String in) {
    if (in == null) return null;
    switch (this) {
      case LOWER_CASE:
        return in.toLowerCase();
      case UPPER_CASE:
        return in.toUpperCase();
    }
    return in;
  }

  public static final String COMMA_SPC = ", ";

  public static List<String> propNames(MtDescriptor<?> d, boolean withPk) {
    return d.getFields(withPk).stream()
      .map(MtFieldDescriptor::getFieldName)
      .collect(toList());
  }

  public static String propNamesCsv(MtDescriptor<?> d, boolean withPk) {
    return String.join(COMMA_SPC, propNames(d, withPk));
  }

  public static String placeholderCsv(MtDescriptor<?> d, boolean withPk) {
    return propNames(d, withPk).stream().map(k -> format(":%s", k)).collect(joining(COMMA_SPC));
  }

  public static String placeHolderAssignmentCsv(MtDescriptor<?> d, boolean withPk) {
    return propNames(d, withPk).stream().map(k -> format("%s = :%s", k, k)).collect(joining(COMMA_SPC));
  }
}
