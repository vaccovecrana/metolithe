package io.vacco.metolithe.core;

import java.util.*;

public class MtQuery {

  public String preparedStatementQuery;
  public final Map<String, Object> params = new LinkedHashMap<>();
  public final List<String> slots = new ArrayList<>();

  public MtQuery as(String preparedStatementQuery) {
    this.preparedStatementQuery = preparedStatementQuery;
    return this;
  }

  public MtQuery withParam(String name, Object value) {
    params.put(name, value);
    return this;
  }

  public MtQuery withSlotValue(String slotValue) {
    this.slots.add(slotValue);
    return this;
  }

  public String render() {
    String q = preparedStatementQuery;
    for (int i = 0; i < slots.size(); i++) {
      String fieldKey = String.format("$%d", i);
      q = q.replace(fieldKey, slots.get(i));
    }
    return q;
  }

  public static MtQuery of(String preparedStatementQuery) {
    return new MtQuery().as(preparedStatementQuery);
  }

}
