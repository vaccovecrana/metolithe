package io.vacco.metolithe.core;

import java.util.*;
import static java.util.stream.Collectors.*;

public enum MtCaseFormat {

  UPPER_CASE, LOWER_CASE, KEEP_CASE;

  public static final String COMMA_SPC = ", ";

  public static String of(String in, MtCaseFormat format) {
    if (in == null || format == null) return null;
    switch (format) {
      case LOWER_CASE: return in.toLowerCase();
      case UPPER_CASE: return in.toUpperCase();
    }
    return in;
  }

  public static List<String> propNames(MtDescriptor<?> d, MtCaseFormat f, boolean withPk) {
    return d.getFields(withPk).stream()
        .map(fd -> of(fd.getField().getName(), f))
        .collect(toList());
  }

  public static String propNamesCsv(MtDescriptor<?> d, MtCaseFormat f, boolean withPk) {
    return String.join(COMMA_SPC, propNames(d, f, withPk));
  }

  public static String placeholderCsv(MtDescriptor<?> d, MtCaseFormat f, boolean withPk) {
    return propNames(d, f, withPk).stream().map(k -> String.format(":%s", k)).collect(joining(COMMA_SPC));
  }

  public static String placeHolderAssignmentCsv(MtDescriptor<?> d, MtCaseFormat f, boolean withPk) {
    return propNames(d, f, withPk).stream()
        .map(k -> String.format("%s = :%s", k, k))
        .collect(joining(COMMA_SPC));
  }
}
