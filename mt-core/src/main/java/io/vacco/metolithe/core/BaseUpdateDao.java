package io.vacco.metolithe.core;

import org.codejargon.fluentjdbc.api.FluentJdbc;
import java.util.Map;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.Objects.*;

public abstract class BaseUpdateDao<T> extends BaseDao<T> {

  public BaseUpdateDao(Class<T> clazz, FluentJdbc jdbc, String sourceSchema, EntityDescriptor.CaseFormat format) {
    super(clazz, jdbc, sourceSchema, format);
  }

  private String getUpdateQuery() {
    String queryAssignments = getDescriptor().placeHolderAssignmentCsv(false);
    return getQueryCache().computeIfAbsent("update",
        k -> format("update %s set %s where %s = :%s", getSchemaName(),
            queryAssignments, getPrimaryKeyId(), getPrimaryKeyId())
    );
  }

  private String getDeleteQuery() {
    return getQueryCache().computeIfAbsent("delete",
        k -> format("delete from %s where %s = :%s",
            getSchemaName(), getPrimaryKeyId(), getPrimaryKeyId()));
  }

  private String getDeleteWhereEqQuery(String field) {
    return getQueryCache().computeIfAbsent("deleteWhereEq" + field,
        k -> format("delete from %s where %s = :%s", getSchemaName(), field, field));
  }

  public T update(T record) {
    requireNonNull(record, classError(DaoError.MISSING_DATA));
    String query = getUpdateQuery();
    Map<String, Object> params = getDescriptor().extractAll(record, Function.identity(), true);
    sql().query().update(query).namedParams(params).run();
    return record;
  }

  public T merge(T record) {
    String id = getDescriptor().extract(record, getPrimaryKeyId());
    if (!load(id).isPresent()) { return save(record); }
    return update(record);
  }

  public long delete(T record) {
    requireNonNull(record, classError(DaoError.MISSING_DATA));
    String id = getDescriptor().extract(record, getPrimaryKeyId());
    return sql().query().update(getDeleteQuery())
        .namedParam(getPrimaryKeyId(), id).run().affectedRows();
  }

  public long deleteWhereEq(String field, String value) {
    return sql().query().update(getDeleteWhereEqQuery(field))
        .namedParam(field, value).run().affectedRows();
  }
}
