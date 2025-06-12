package io.vacco.metolithe.dao;

import io.vacco.metolithe.core.MtDescriptor;
import io.vacco.metolithe.core.MtFieldDescriptor;
import java.util.*;
import java.util.stream.Collectors;

import static io.vacco.metolithe.core.MtErr.*;

public class MtQuery {

  public enum LogicalOperator { AND, OR }

  private final Set<MtPredicate>        predicates = new LinkedHashSet<>();
  private final List<LogicalOperator>   operators = new ArrayList<>();
  private final Map<String, Object>     params = new LinkedHashMap<>();
  private final List<MtFieldDescriptor> orderByFields = new ArrayList<>();
  private final List<MtJoin>            joins = new ArrayList<>();
  private final String                  schema;
  private boolean orderByReverse;

  private MtQuery(String schema) {
    this.schema = Objects.requireNonNull(schema);
  }

  public static MtQuery create(String schema) {
    return new MtQuery(schema);
  }

  private MtQuery addPredicate(MtPredicate predicate) {
    predicates.add(predicate);
    predicate.applyParams(params);
    return this;
  }

  private String nextParamName() {
    return "p" + params.size();
  }

  public MtQuery eq(MtFieldDescriptor field, Object value) {
    return addPredicate(MtPredicate.filter(field, MtPredicate.Operator.EQ, value, nextParamName()));
  }

  public MtQuery neq(MtFieldDescriptor field, Object value) {
    return addPredicate(MtPredicate.filter(field, MtPredicate.Operator.NEQ, value, nextParamName()));
  }

  public MtQuery isNull(MtFieldDescriptor field) {
    return addPredicate(MtPredicate.filter(field, MtPredicate.Operator.IS_NULL));
  }

  public MtQuery isNotNull(MtFieldDescriptor field) {
    return addPredicate(MtPredicate.filter(field, MtPredicate.Operator.IS_NOT_NULL));
  }

  public MtQuery lt(MtFieldDescriptor field, Object value) {
    return addPredicate(MtPredicate.filter(field, MtPredicate.Operator.LT, value, nextParamName()));
  }

  public MtQuery lte(MtFieldDescriptor field, Object value) {
    return addPredicate(MtPredicate.filter(field, MtPredicate.Operator.LTE, value, nextParamName()));
  }

  public MtQuery gt(MtFieldDescriptor field, Object value) {
    return addPredicate(MtPredicate.filter(field, MtPredicate.Operator.GT, value, nextParamName()));
  }

  public MtQuery gte(MtFieldDescriptor field, Object value) {
    return addPredicate(MtPredicate.filter(field, MtPredicate.Operator.GTE, value, nextParamName()));
  }

  public MtQuery like(MtFieldDescriptor field, String value) {
    return addPredicate(MtPredicate.filter(field, MtPredicate.Operator.LIKE, value, nextParamName()));
  }

  public MtQuery innerJoin(MtDescriptor<?> source, MtDescriptor<?> target) {
    joins.add(MtJoin.inner(this.schema, source, target));
    return this;
  }

  public MtQuery leftJoin(MtDescriptor<?> source, MtDescriptor<?> target) {
    joins.add(MtJoin.left(this.schema, source, target));
    return this;
  }

  public MtQuery seek(MtFieldDescriptor[] fields, Object[] values, boolean reverse) {
    if (fields.length != values.length) {
      throw badSeek();
    }
    orderByFields.clear();
    orderByFields.addAll(Arrays.asList(fields));
    orderByReverse = reverse;
    for (int i = 0; i < fields.length; i++) {
      if (values[i] != null) {
        var op = reverse ? MtPredicate.Operator.LTE : MtPredicate.Operator.GTE;
        addPredicate(MtPredicate.seek(fields[i], op, values[i], "sk" + i));
      }
    }
    return this;
  }

  public MtQuery and() {
    if (!predicates.isEmpty() && predicates.iterator().next().isSeek) {
      throw badLogic();
    }
    operators.add(LogicalOperator.AND);
    return this;
  }

  public MtQuery or() {
    if (!predicates.isEmpty() && predicates.iterator().next().isSeek) {
      throw badLogic();
    }
    operators.add(LogicalOperator.OR);
    return this;
  }

  public String renderFilter() {
    var filterPreds = predicates.stream()
      .filter(p -> !p.isSeek)
      .collect(Collectors.toList());
    if (filterPreds.isEmpty()) {
      return "";
    }
    var sb = new StringBuilder();
    for (int i = 0; i < filterPreds.size(); i++) {
      sb.append(filterPreds.get(i).render());
      if (i < Math.min(operators.size(), filterPreds.size() - 1)) {
        sb.append(" ").append(operators.get(i).name()).append(" ");
      }
    }
    return sb.toString();
  }

  public String renderSeek() {
    var seekPreds = predicates.stream()
      .filter(p -> p.isSeek)
      .collect(Collectors.toList());
    if (seekPreds.isEmpty()) {
      return "";
    }
    var fieldsCsv = seekPreds.stream()
      .map(p -> p.field0.getFieldNameAliased())
      .collect(Collectors.joining(", "));
    var paramsCsv = seekPreds.stream()
      .map(p -> ":" + p.paramName) // Only use parameter placeholder, e.g., :sk0
      .collect(Collectors.joining(", "));
    var op = orderByReverse ? "<=" : ">=";
    return String.format("and (%s) %s (%s)", fieldsCsv, op, paramsCsv);
  }

  public String renderOrderBy() {
    return orderByFields.stream()
      .map(fd -> String.format("%s %s", fd.getFieldNameAliased(), orderByReverse ? "desc" : "asc"))
      .collect(Collectors.joining(", "));
  }

  public String renderJoins() {
    return joins.stream()
      .map(MtJoin::render)
      .collect(Collectors.joining(" "));
  }

  public Map<String, Object> getParams() {
    return params;
  }

}
