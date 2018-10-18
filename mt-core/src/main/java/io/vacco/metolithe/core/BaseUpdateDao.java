package io.vacco.metolithe.core;

import io.vacco.metolithe.spi.MtIdGenerator;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Objects.*;

public abstract class BaseUpdateDao<T, K> extends BaseDao<T, K> {

  public BaseUpdateDao(Class<T> clazz, FluentJdbc jdbc, String sourceSchema,
                       EntityDescriptor.CaseFormat format, MtIdGenerator<K> idGenerator) {
    super(clazz, jdbc, sourceSchema, format, idGenerator);
  }

  private String getUpdateQuery() {
    String queryAssignments = getDescriptor().placeHolderAssignmentCsv(false);
    return getQueryCache().computeIfAbsent("update",
        k -> format("update %s set %s where %s = :%s", getSchemaName(), queryAssignments,
            getDescriptor().getPrimaryKeyField(), getDescriptor().getPrimaryKeyField())
    );
  }

  private String getDeleteQuery() {
    return getQueryCache().computeIfAbsent("delete",
        k -> format("delete from %s where %s = :%s",
            getSchemaName(), getDescriptor().getPrimaryKeyField(),
            getDescriptor().getPrimaryKeyField()));
  }

  private String getDeleteWhereEqQuery(String field) {
    return getQueryCache().computeIfAbsent("deleteWhereEq" + field,
        k -> format("delete from %s where %s = :%s", getSchemaName(), field, field));
  }

  public T update(T record) {
    requireNonNull(record, classError(DaoError.MISSING_DATA));
    setId(record);
    String query = getUpdateQuery();
    Map<String, Object> params = getDescriptor().extractAll(record, true);
    sql().query().update(query).namedParams(params).run();
    return record;
  }

  public T merge(T record) {
    K id = idOf(record);
    if (!load(id).isPresent()) { return save(record); }
    return update(record);
  }

  public long delete(T record) {
    requireNonNull(record, classError(DaoError.MISSING_DATA));
    K id = idOf(record);
    return sql().query().update(getDeleteQuery())
        .namedParam(getDescriptor().getPrimaryKeyField(), id).run().affectedRows();
  }

  public long deleteWhereEq(String field, String value) {
    return sql().query().update(getDeleteWhereEqQuery(field))
        .namedParam(field, value).run().affectedRows();
  }
}
