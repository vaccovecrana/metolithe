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

  protected String getSelectWhereEqQuery(String field) {
    String fn = dsc.getFormat().of(field);
    return getQueryCache().computeIfAbsent("selectWhereEq" + fn,
        k -> format("select %s from %s where %s = :%s",
            propNamesCsv(dsc, true), getSchemaName(), fn, fn)
    );
  }

  public Optional<T> load(K id) {
    Optional<MtFieldDescriptor> pkf = dsc.getPkField();
    if (pkf.isPresent()) {
      return sql().query().select(getSelectWhereEqQuery(pkf.get().getFieldName()))
          .namedParam(pkf.get().getFieldName(), id)
          .firstResult(mapToDefault());
    }
    return Optional.empty();
  }

  public Collection<T> loadWhereEq(String field, Object value) {
    return sql().query()
        .select(getSelectWhereEqQuery(field))
        .namedParam(dsc.getFormat().of(field), value)
        .listResult(mapToDefault());
  }

  public <V extends Enum<V>> Collection<T> loadWhereEnEq(String fieldName, V value) {
    return loadWhereEq(fieldName, value);
  }

  @SafeVarargs
  public final <V> Map<V, List<T>> loadWhereIn(String field, V... values) {
    if (values == null || values.length == 0) { return Collections.emptyMap(); }
    Optional<MtFieldDescriptor> ofd = dsc.getField(field);
    if (ofd.isPresent()) {
      Map<String, Object> pids = toNamedParamMap(asList(values), field);
      String query = String.format("select %s from %s where %s in (%s)",
          propNamesCsv(dsc, true), getSchemaName(), field, toNamedParamLabels(pids)
      );
      List<T> raw = sql().query().select(query).namedParams(pids).listResult(mapToDefault());
      return raw.stream().collect(groupingBy(r -> (V) ofd.get().getValue(r)));
    }
    return emptyMap();
  }

  @SafeVarargs
  public final <V extends Enum<V>> Map<V, List<T>> loadWhereEnIn(String field, V... enums) {
    return loadWhereIn(field, enums);
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
}
