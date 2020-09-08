package io.vacco.metolithe.core;

import org.codejargon.fluentjdbc.api.FluentJdbc;

import java.util.*;

import static io.vacco.metolithe.core.MtCaseFormat.*;
import static java.lang.String.*;
import static java.util.stream.Collectors.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;

public class MtReadDao<T, K> extends MtDao<T, K> {

  public MtReadDao(String schemaName, FluentJdbc jdbc, MtDescriptor<T> d, MtIdFn<K> idFn) {
    super(schemaName, jdbc, d, idFn);
  }

  private String getInsertQuery() {
    return getQueryCache().computeIfAbsent("insert", k ->
        format("insert into %s (%s) values (%s)", getSchemaName(),
            propNamesCsv(dsc, true), placeholderCsv(dsc, true)
        )
    );
  }

  protected String getSelectWhereEqQuery(String field) {
    return getQueryCache().computeIfAbsent("selectWhereEq" + field,
        k -> format("select %s from %s where %s = :%s",
            propNamesCsv(dsc, true), getSchemaName(), field, field)
    );
  }

  private String getSelectByIdQuery() {
    return getSelectWhereEqQuery(dsc.getName());
  }

  public Optional<T> load(K id) {
    Optional<MtFieldDescriptor> pkf = dsc.getPkField();
    if (pkf.isPresent()) {
      return sql().query().select(getSelectByIdQuery())
          .namedParam(pkf.get().getFieldName(), id)
          .firstResult(mapToDefault());
    }
    return Optional.empty();
  }

  public Collection<T> loadWhereEq(String field, Object value) {
    return sql().query()
        .select(getSelectWhereEqQuery(field))
        .namedParam(field, value)
        .listResult(mapToDefault());
  }

  public Collection<T> loadWhereEnEq(Enum<?> fieldName, Object value) {
    return loadWhereEq(fieldName.name(), value);
  }

  @SafeVarargs
  public final <V> Map<V, List<T>> loadWhereIn(String field, V... vals) {
    if (vals == null || vals.length == 0) { return Collections.emptyMap(); }
    Optional<MtFieldDescriptor> ofd = dsc.getField(field);
    if (ofd.isPresent()) {
      Map<String, Object> pids = toNamedParamMap(asList(vals), field);
      String query = String.format("select %s from %s where %s in (%s)",
          propNamesCsv(dsc, true), getSchemaName(), field, toNamedParamLabels(pids)
      );
      List<T> raw = sql().query().select(query).namedParams(pids).listResult(mapToDefault());
      return raw.stream().collect(groupingBy(r -> (V) ofd.get().getValue(r)));
    }
    return emptyMap();
  }

  @SafeVarargs
  public final <V extends Enum<V>> Map<V, List<T>> loadWhereEnIn(V fieldName, V... enumVals) {
    return loadWhereIn(fieldName.toString(), enumVals);
  }

  public T loadExisting(K id) {
    Optional<T> record = load(id);
    if (!record.isPresent()) { throw new MtException.MtMissingIdException(id); }
    return record.get();
  }

  public Map<String, Object> toNamedParamMap(Collection<?> input, String paramLabel) {
    Map<String, Object> pMap = new LinkedHashMap<>();
    List<?> paramList = new ArrayList<>(input);
    for (int k = 0; k < input.size(); k++) {
      String param = format("%s%s", paramLabel, k);
      pMap.put(param, paramList.get(k));
    }
    return pMap;
  }

  public String toNamedParamLabels(Map<String, Object> targetParams) {
    return targetParams.keySet().stream()
        .map(param -> format(":%s", param))
        .collect(joining(", "));
  }
/*
  public <J> J inTransaction(UnsafeSupplier<J> processor) {
    return sql().query().transaction().in(() -> {
      try { return processor.get(); }
      catch (Exception e) { throw new IllegalStateException(e); }
    });
  }
*/
}
