package io.vacco.metolithe.core;

import io.vacco.metolithe.spi.MtCodec;
import io.vacco.metolithe.spi.UnsafeSupplier;
import org.codejargon.fluentjdbc.api.FluentJdbc;

import java.util.*;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.Objects.*;

public abstract class BaseDao<T> extends BaseQueryFactory<T> {

  public enum DaoError { MISSING_DATA, MISSING_ID }

  public BaseDao(FluentJdbc jdbc, MtCodec codec, String sourceSchema, EntityDescriptor.CaseFormat format) {
    super(jdbc, codec, sourceSchema, format);
  }

  private String getInsertQuery() {
    return getQueryCache().computeIfAbsent("insert", k ->
        format("insert into %s (%s) values (%s)", getSchemaName(),
            getDescriptor().propertyNamesCsv(true),
            getDescriptor().placeholderCsv(true)));
  }

  protected String getSelectWhereEqQuery(String field) {
    return getQueryCache().computeIfAbsent("selectWhereEq" + field,
        k -> format("select %s from %s where %s = :%s",
            getDescriptor().propertyNamesCsv(true),
            getSchemaName(), field, field));
  }
  private String getSelectQuery() { return getSelectWhereEqQuery(getPrimaryKeyId()); }

  public T save(T record) {
    requireNonNull(record, classError(DaoError.MISSING_DATA));
    String query = getInsertQuery();
    Map<String, Object> namedParams = getDescriptor().extractAll(record, this::serialize, true);
    sql().query().update(query).namedParams(namedParams).run();
    return record;
  }

  public Optional<T> load(String id) {
    requireNonNull(id, classError(DaoError.MISSING_ID));
    return sql().query().select(getSelectQuery())
        .namedParam(getPrimaryKeyId(), id)
        .firstResult(mapToDefault());
  }

  public Collection<T> loadWhereEq(String field, String value) {
    requireNonNull(field, classError(DaoError.MISSING_ID));
    requireNonNull(value, classError(DaoError.MISSING_DATA));
    return sql().query().select(getSelectWhereEqQuery(field))
        .namedParam(field, value).listResult(mapToDefault());
  }

  public T loadExisting(String id) {
    Optional<T> record = load(id);
    if (!record.isPresent()) {
      throw new IllegalArgumentException(classError(DaoError.MISSING_ID));
    }
    return record.get();
  }

  public String toNamedParam(Map<String, Object> targetParams, Collection<?> input, String paramLabel) {
    List<?> paramList = new ArrayList<>(input);
    return IntStream.range(0, input.size())
        .mapToObj(i -> {
          String param = format("%s%s", paramLabel, i);
          String paramLabel0 = format(":%s", param);
          targetParams.put(param, paramList.get(i));
          return paramLabel0;
        }).collect(joining(", "));
  }

  public <K> K inTransaction(UnsafeSupplier<K> processor) {
    return sql().query().transaction().in(() -> {
      try { return processor.get(); }
      catch (Exception e) { throw new IllegalStateException(e); }
    });
  }
}