package io.vacco.metolithe.core;

import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.query.SelectQuery;
import java.util.*;

import static io.vacco.metolithe.core.MtCaseFormat.*;
import static java.lang.String.*;
import static java.util.stream.Collectors.*;
import static java.util.Arrays.*;

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

  @SafeVarargs
  @SuppressWarnings("varargs")
  public final <V> Map<V, List<T>> loadWhereIn(String field, V ... values) {
    if (values == null || values.length == 0) { return Collections.emptyMap(); }
    MtFieldDescriptor fd = dsc.getField(field);
    Map<String, Object> pids = toNamedParamMap(asList(values), fd.getFieldName());
    String query = format("select %s from %s where %s in (%s)",
        propNamesCsv(dsc, true), getSchemaName(), fd.getFieldName(), toNamedParamLabels(pids)
    );
    List<T> raw = sql().query().select(query).namedParams(pids).listResult(mapToDefault());
    return raw.stream().collect(groupingBy(fd::getValue));
  }

  public T loadExisting(K id) {
    Optional<T> record = load(id);
    if (record.isEmpty()) { throw new MtException.MtMissingIdException(id); }
    return record.get();
  }

  public <V> MtPage<T, V> loadPage(V nextIdx, MtQuery filterQuery, String sortField, int pageSize) {
    try {
      MtFieldDescriptor sf = dsc.getField(sortField);
      MtPage<T, V> p = new MtPage<>();
      String countFilterQuery = filterQuery == null ? "" : format("where (%s)", filterQuery.render());
      String countQuery = format("select count(*) from %s %s", getSchemaName(), countFilterQuery);

      SelectQuery cq = sql().query().select(countQuery);
      if (filterQuery != null) {
        for (Map.Entry<String, Object> e : filterQuery.params.entrySet()) {
          cq = cq.namedParam(e.getKey(), e.getValue());
        }
      }

      Optional<Long> count = cq.firstResult(Mappers.singleLong());
      int rawSize = pageSize + 1;

      if (count.isPresent()) {
        String filterClause = filterQuery == null ? "" : format("and (%s)", filterQuery.render());
        String sortClause = nextIdx == null ? "" : new MtQuery().as("and ($0 >= :$0)").withSlotValue(sf.getFieldName()).render();
        String query = format("select %s from %s where 1=1 %s %s order by %s limit %s",
            propNamesCsv(dsc, true), getSchemaName(),
            sortClause, filterClause, sf.getFieldName(), rawSize
        );

        SelectQuery q = sql().query().select(query);
        if (nextIdx != null) {
          q = q.namedParam(sf.getFieldName(), nextIdx);
        }
        if (filterQuery != null) {
          for (Map.Entry<String, Object> e : filterQuery.params.entrySet()) {
            q = q.namedParam(e.getKey(), e.getValue());
          }
        }

        List<T> data = q.listResult(mapToDefault());
        V next = data.size() == rawSize ? sf.getValue(data.get(data.size() - 1)) : null;
        p.data = data.subList(0, data.size() == rawSize ? rawSize - 1 : data.size());
        p.totalSize = count.get();
        p.next = next;
      }
      return p;
    } catch (Exception e) {
      throw new MtException.MtPageAccessException(sortField, nextIdx, dsc, e);
    }
  }

  public <V> MtPage<T, V> loadPage(V nextIdx, String sortField, int pageSize) {
    return loadPage(nextIdx, null, sortField, pageSize);
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
