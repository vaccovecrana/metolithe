package io.vacco.metolithe.core;

import io.vacco.metolithe.util.*;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import java.util.*;
import java.util.stream.IntStream;

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
      var pNames = propNamesCsv(dsc, true);
      return format("select %s from %s where %s = :%s", pNames, getSchemaName(), fn, fn);
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

  public Collection<T> loadWhereEq(String field, Object value) {
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
    var query = format("select %s from %s where %s in (%s)",
      propNamesCsv(dsc, true), getSchemaName(), fd.getFieldName(), toNamedParamLabels(pids)
    );
    var raw = sql().query().select(query).namedParams(pids).listResult(mapToDefault());
    return raw.stream().collect(groupingBy(fd::getValue));
  }

  public T loadExisting(K id) {
    var record = load(id);
    if (record.isEmpty()) {
      throw new MtException.MtMissingIdException(id);
    }
    return record.get();
  }

  private boolean allNonNull(Object[] oa) {
    for (Object o : oa) {
      if (o == null) return false;
    }
    return true;
  }

  public List<T> loadPageItems(int pageSize, MtQuery filter, String[] nxf, Object[] nxv) {
    try {
      var qFmt = String.join("\n", "",
        "select %s from %s where 1=1", // property names, schema name
        "%s", // filter predicate
        "%s", // seek predicate
        "order by %s limit %s"
      );
      var filterP = filter == null ? "" : format("and (%s)", filter.render());
      var nxNn = allNonNull(nxv);
      var seekP = "";
      var sk = stream(nxf).map(dsc::getField).toArray(MtFieldDescriptor[]::new);
      var skCsv = stream(sk)
        .map(MtFieldDescriptor::getFieldName)
        .collect(joining(", "));

      if (nxNn) {
        var skPar = IntStream.range(0, nxv.length)
          .mapToObj(i -> format(":sk%d", i))
          .collect(joining(", "));
        seekP = format("and (%s) >= (%s)", skCsv, skPar);
      }

      var sql = format(qFmt,
        propNamesCsv(dsc, true), getSchemaName(),
        filterP, seekP, skCsv, pageSize + 1
      );
      var q = sql().query().select(sql);

      if (nxNn) {
        for (int i = 0; i < nxv.length; i++) {
          q = q.namedParam(format("sk%d", i), nxv[i]);
        }
      }
      if (filter != null) {
        for (var e : filter.params.entrySet()) {
          q = q.namedParam(e.getKey(), e.getValue());
        }
      }
      return new ArrayList<>(q.listResult(mapToDefault()));
    }
    catch (Exception e) {
      throw new MtException.MtPageAccessException(nxf, nxv, dsc, e);
    }
  }

  public <K1> MtPage1<T, K1> loadPage1(int pageSize, MtQuery filter,
                                       String nx1Fld, K1 nx1) {
    var page = new MtPage1<T, K1>();
    var items = loadPageItems(pageSize, filter, new String[] {nx1Fld}, new Object[] {nx1});
    page.items = items;
    if (items.size() > pageSize) {
      var next = items.remove(items.size() - 1);
      page.nx1 = dsc.getField(nx1Fld).getValue(next);
    }
    page.size = items.size();
    return page;
  }

  public <K1, K2> MtPage2<T, K1, K2> loadPage2(int pageSize, MtQuery filter,
                                               String nx1Fld, K1 nx1,
                                               String nx2Fld, K2 nx2) {
    var page = new MtPage2<T, K1, K2>();
    var items = loadPageItems(pageSize, filter, new String[] {nx1Fld, nx2Fld}, new Object[] {nx1, nx2});
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
