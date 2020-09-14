package io.vacco.metolithe.core;

import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.mapper.Mappers;

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
    if (!record.isPresent()) { throw new MtException.MtMissingIdException(id); }
    return record.get();
  }

  public <V> MtKeysetPage<T, V> loadPage(V indexPage, String sortField, int pageSize) {
    try {
      MtFieldDescriptor srt = dsc.getField(sortField);
      MtKeysetPage<T, V> p = new MtKeysetPage<>();
      String countQuery = format("select count(*) from %s", getSchemaName());
      Optional<Long> count = sql().query().select(countQuery).firstResult(Mappers.singleLong());
      int rawSize = pageSize + 1;
      if (count.isPresent()) {
        String whereClause = indexPage == null ? " " : format(" where %s >= :%s ", srt.getFieldName(), srt.getFieldName());
        String query = format("select %s from %s%sorder by %s limit %s",
            propNamesCsv(dsc, true), getSchemaName(),
            whereClause, srt.getFieldName(), rawSize
        );
        List<T> data = sql().query().select(query).namedParam(srt.getFieldName(), indexPage).listResult(mapToDefault());
        V next = data.size() == rawSize ? srt.getValue(data.get(data.size() - 1)) : null;
        p.data = data.subList(0, data.size() == rawSize ? rawSize - 1 : data.size());
        p.totalSize = count.get();
        p.next = next;
      }
      return p;
    } catch (Exception e) {
      throw new MtException.MtPageAccessException(sortField, indexPage, dsc, e);
    }
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
