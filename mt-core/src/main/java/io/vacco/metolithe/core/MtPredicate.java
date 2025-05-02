package io.vacco.metolithe.core;

import java.util.*;

public class MtPredicate {

  public enum Operator {
    EQ(" = :%s"), NEQ(" != :%s"), IS_NULL(" IS NULL"), IS_NOT_NULL(" IS NOT NULL"),
    LT(" < :%s"), LTE(" <= :%s"), GT(" > :%s"), GTE(" >= :%s"),
    LIKE(" LIKE :%s");

    private final String sqlTemplate;

    Operator(String sqlTemplate) { this.sqlTemplate = sqlTemplate; }
    public String render(String paramName) {
      return String.format(sqlTemplate, paramName);
    }
  }

  private final Operator operator;
  private final Object value;

  public  final MtFieldDescriptor field;
  public  final String paramName;
  public  final boolean isSeek;

  private MtPredicate(MtFieldDescriptor field, Operator operator, Object value, String paramName, boolean isSeek) {
    this.field = Objects.requireNonNull(field);
    this.operator = Objects.requireNonNull(operator);
    this.value = value;
    this.paramName = paramName;
    this.isSeek = isSeek;
  }

  public static MtPredicate filter(MtFieldDescriptor field, Operator operator, Object value, String paramName) {
    return new MtPredicate(field, operator, value, paramName, false);
  }

  public static MtPredicate filter(MtFieldDescriptor field, Operator operator) {
    if (operator != Operator.IS_NULL && operator != Operator.IS_NOT_NULL) {
      throw new IllegalArgumentException("Value required for operator: " + operator);
    }
    return new MtPredicate(field, operator, null, null, false);
  }

  public static MtPredicate seek(MtFieldDescriptor field, Operator operator, Object value, String paramName) {
    if (operator != Operator.GT && operator != Operator.GTE && operator != Operator.LT && operator != Operator.LTE) {
      throw new IllegalArgumentException("Seek predicates only support GT, GTE, LT, LTE operators");
    }
    return new MtPredicate(field, operator, value, paramName, true);
  }

  public String render() {
    var fieldName = field.getFieldName();
    return operator == Operator.IS_NULL || operator == Operator.IS_NOT_NULL
      ? fieldName + operator.render("")
      : fieldName + operator.render(paramName);
  }

  public void applyParams(Map<String, Object> params) {
    if (paramName != null && value != null) {
      params.put(paramName, value);
    }
  }

  @Override public String toString() {
    return String.format("(%s): %s", isSeek ? "skp" : "flp", this.render());
  }

  @Override public boolean equals(Object obj) {
    return obj instanceof MtPredicate
      && ((MtPredicate) obj).render().equals(this.render());
  }

  @Override public int hashCode() {
    return this.render().hashCode();
  }

}
