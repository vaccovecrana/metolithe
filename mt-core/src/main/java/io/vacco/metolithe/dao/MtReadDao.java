package io.vacco.metolithe.dao;

import io.vacco.metolithe.core.*;
import io.vacco.metolithe.id.MtIdFn;
import io.vacco.metolithe.util.*;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import java.util.*;

import static io.vacco.metolithe.core.MtErr.*;
import static io.vacco.metolithe.core.MtCaseFormat.*;
import static java.lang.String.*;
import static java.util.stream.Collectors.*;
import static java.util.Arrays.*;

public class MtReadDao<T, K> extends MtDao<T, K> {

  public MtReadDao(String schemaName, FluentJdbc jdbc, MtDescriptor<T> d, MtIdFn<K> idFn) {
    super(schemaName, jdbc, d, idFn);
  }

  protected String getSelectWhereEqQuery(String field) {
    var fn = dsc.getFormat().of(field);
    var qk = "selectWhereEq" + fn;
    return getQueryCache().computeIfAbsent(qk, k -> {
      var alias = dsc.getAlias();
      var pNames = propNamesCsv(dsc, true, alias);
      return format(
        "select %s from %s %s where %s.%s = :%s",
        pNames, getTableName(), alias, alias, fn, fn
      );
    });
  }

  public Optional<T> load(K id) {
    var pkf = dsc.getPkField();
    if (pkf.isPresent()) {
      var q = getSelectWhereEqQuery(pkf.get().getFieldName());
      var fn = pkf.get().getFieldName();
      return sql().query().select(q)
        .namedParam(fn, id)
        .firstResult(mapToDefault());
    }
    return Optional.empty();
  }

  public List<T> loadWhereEq(String field, Object value) {
    return sql().query()
      .select(getSelectWhereEqQuery(field))
      .namedParam(dsc.getFormat().of(field), value)
      .listResult(mapToDefault());
  }

  @SafeVarargs
  @SuppressWarnings("varargs")
  public final <V> Map<V, List<T>> loadWhereIn(String field, V ... values) {
    if (values == null || values.length == 0) {
      return Collections.emptyMap();
    }
    var fd = dsc.getField(field);
    var pids = toNamedParamMap(asList(values), fd.getFieldName());
    var alias = dsc.getAlias();
    var query = String.format(
      "select %s from %s %s where %s in (%s)",
      propNamesCsv(dsc, true, alias), getTableName(),
      alias, fd.getFieldName(), toNamedParamLabels(pids)
    );
    var raw = sql().query().select(query).namedParams(pids).listResult(mapToDefault());
    return raw.stream().collect(groupingBy(fd::getValue));
  }

  public T loadExisting(K id) {
    var record = load(id);
    if (record.isEmpty()) {
      throw badId(id);
    }
    return record.get();
  }

  public List<T> loadPageItems(int pageSize, MtQuery query) {
    try {
      var qFmt = String.join("\n",
        "select %s from %s %s", // props, table, alias
        "%s", // join clause
        "where 1 = 1",
        "%s", // filter predicate
        "%s", // seek predicate
        "order by %s limit %d"
      );
      var joins = query.renderJoins();
      var filterP = query.renderFilter().isEmpty() ? "" : format("and (%s)", query.renderFilter());
      var seekP = query.renderSeek();
      var orderBy = query.renderOrderBy();
      var alias = dsc.getAlias();
      var sql = format(qFmt,
        propNamesCsv(dsc, true, alias),
        getTableName(), alias,
        joins, filterP, seekP, orderBy, pageSize + 1
      );
      var q = sql().query().select(sql);
      for (var e : query.getParams().entrySet()) {
        q = q.namedParam(e.getKey(), e.getValue());
      }
      return new ArrayList<>(q.listResult(mapToDefault()));
    } catch (Exception e) {
      throw badPageAccess(null, null, dsc, e);
    }
  }

  public MtQuery query() {
    return MtQuery.create(this.schema);
  }

  private MtQuery setQuery(MtQuery filter, String[] nxFields, Object[] nxValues, boolean reverse) {
    var fields = new MtFieldDescriptor[nxFields.length];
    for (int i = 0; i < nxFields.length; i++) {
      fields[i] = dsc.getField(nxFields[i]);
    }
    if (filter == null) {
      filter = query();
    }
    return filter.seek(fields, nxValues, reverse);
  }

  public <K1> MtPage1<T, K1> loadPage1(int pageSize, boolean reverse, MtQuery filter, String nx1Fld, K1 nx1) {
    var query = setQuery(filter, new String[] {nx1Fld}, new Object[] {nx1}, reverse);
    var page = new MtPage1<T, K1>();
    var items = loadPageItems(pageSize, query);
    page.items = items;
    if (items.size() > pageSize) {
      var next = items.remove(items.size() - 1);
      page.nx1 = dsc.getField(nx1Fld).getValue(next);
    }
    page.size = items.size();
    return page;
  }

  public <K1, K2> MtPage2<T, K1, K2> loadPage2(int pageSize, boolean reverse,
                                               MtQuery filter,
                                               String nx1Fld, K1 nx1,
                                               String nx2Fld, K2 nx2) {
    var query = setQuery(filter, new String[] {nx1Fld, nx2Fld}, new Object[] {nx1, nx2}, reverse);
    var page = new MtPage2<T, K1, K2>();
    var items = loadPageItems(pageSize, query);
    page.items = items;
    if (items.size() > pageSize) {
      var next = items.remove(items.size() - 1);
      page.nx1 = dsc.getField(nx1Fld).getValue(next);
      page.nx2 = dsc.getField(nx2Fld).getValue(next);
    }
    page.size = items.size();
    return page;
  }

  public Map<String, Object> toNamedParamMap(Collection<?> input, String paramLabel) {
    var pMap = new LinkedHashMap<String, Object>();
    var paramList = new ArrayList<>(input);
    for (int k = 0; k < input.size(); k++) {
      var param = format("%s%s", paramLabel, k);
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
