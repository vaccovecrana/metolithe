package io.vacco.metolithe.core;

import org.codejargon.fluentjdbc.api.FluentJdbc;
import java.util.*;
import java.util.function.BiFunction;

import static io.vacco.metolithe.core.MtCaseFormat.*;
import static java.lang.String.format;

public class MtWriteDao<T, K> extends MtReadDao<T, K> {

  public MtWriteDao(String schemaName, FluentJdbc jdbc, MtDescriptor<T> d, MtIdFn<K> idFn) {
    super(schemaName, jdbc, d, idFn);
  }

  @SuppressWarnings("unchecked")
  public <V> V withId(T target, BiFunction<MtFieldDescriptor, K, V> bfn) {
    Optional<MtFieldDescriptor> opk = dsc.getPkField();
    Object[] pkVals = dsc.getPkValues(target);
    if (opk.isPresent()) {
      K id = pkVals.length == 0 ? (K) opk.get().getValue(target) : idFn.apply(pkVals);
      return bfn.apply(opk.get(), id);
    } else {
      return bfn.apply(null, null);
    }
  }

  public Optional<K> idOf(T test) {
    return withId(test, (fd, id) -> id == null
      ? Optional.empty()
      : Optional.of(id)
    );
  }

  public T save(T rec) {
    return withId(rec, (fd, pk) -> {
      if (fd != null) {
        fd.setValue(rec, pk);
      }
      String query = getQueryCache().computeIfAbsent("insert", k ->
        format("insert into %s (%s) values (%s)", getSchemaName(),
          propNamesCsv(dsc, true), placeholderCsv(dsc, true)
        )
      );
      Map<String, Object> namedParams = dsc.getAll(rec);
      sql().query().update(query).namedParams(namedParams).run();
      return rec;
    });
  }

  public T update(T rec) {
    return withId(rec, (fd, pk) -> {
      String queryAssignments = placeHolderAssignmentCsv(dsc, false);
      String query = getQueryCache().computeIfAbsent("update",
        k -> format("update %s set %s where %s = :%s", getSchemaName(), queryAssignments,
          fd.getFieldName(), fd.getFieldName())
      );
      Map<String, Object> params = dsc.getAll(rec);
      sql().query().update(query).namedParams(params).run();
      return rec;
    });
  }

  public T merge(T rec) {
    return withId(rec, (fd, pk) -> {
      T rec0 = load(pk).isEmpty() ? save(rec) : update(rec);
      if (pk != null && fd != null) {
        fd.setValue(rec0, pk);
      }
      return rec0;
    });
  }

  public long delete(T record) {
    return withId(record, (fd, pk) -> {
      String query = getQueryCache().computeIfAbsent("delete",
        k -> format("delete from %s where %s = :%s", getSchemaName(), fd.getFieldName(), fd.getFieldName()));
      return sql().query().update(query).namedParam(fd.getFieldName(), pk).run().affectedRows();
    });
  }

  public long deleteWhereEq(String field, Object value) {
    String fn = dsc.getFormat().of(field);
    String query = getQueryCache().computeIfAbsent("deleteWhereEq" + fn,
      k -> format("delete from %s where %s = :%s", getSchemaName(), fn, fn));
    return sql().query().update(query).namedParam(fn, value).run().affectedRows();
  }

  public long deleteWhereIdEq(K id) {
    Optional<MtFieldDescriptor> opk = dsc.getPkField();
    return opk.map(mtFieldDescriptor -> deleteWhereEq(mtFieldDescriptor.getFieldName(), id)).orElse(-1L);
  }
}
