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
  public <V> V withId(T rec, BiFunction<MtFieldDescriptor, K, V> bfn) {
    var opk = dsc.getPkField();
    var pkVals = dsc.getPkValues(rec);
    if (opk.isPresent()) {
      K id = pkVals.length == 0 ? (K) opk.get().getValue(rec) : idFn.apply(pkVals);
      opk.get().setValue(rec, id);
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
      var query = getQueryCache().computeIfAbsent("insert", k ->
        format("insert into %s (%s) values (%s)", getSchemaName(),
          propNamesCsv(dsc, true), placeholderCsv(dsc, true)
        )
      );
      var namedParams = dsc.getAll(rec);
      sql().query().update(query).namedParams(namedParams).run();
      return rec;
    });
  }

  public T update(T rec) {
    return withId(rec, (fd, pk) -> {
      var queryAssignments = placeHolderAssignmentCsv(dsc, false);
      var query = getQueryCache().computeIfAbsent("update",
        k -> format("update %s set %s where %s = :%s", getSchemaName(), queryAssignments,
          fd.getFieldName(), fd.getFieldName())
      );
      var params = dsc.getAll(rec);
      sql().query().update(query).namedParams(params).run();
      return rec;
    });
  }

  public T upsert(T rec) {
    return withId(rec, (fd, pk) -> load(pk).isEmpty() ? save(rec) : update(rec));
  }

  public long delete(T rec) {
    return withId(rec, (fd, pk) -> {
      var query = getQueryCache().computeIfAbsent("delete",
        k -> format("delete from %s where %s = :%s", getSchemaName(), fd.getFieldName(), fd.getFieldName()));
      return sql().query().update(query).namedParam(fd.getFieldName(), pk).run().affectedRows();
    });
  }

  public long deleteWhereEq(String field, Object value) {
    var fn = dsc.getFormat().of(field);
    var query = getQueryCache().computeIfAbsent("deleteWhereEq" + fn,
      k -> format("delete from %s where %s = :%s", getSchemaName(), fn, fn));
    return sql().query().update(query).namedParam(fn, value).run().affectedRows();
  }

  public long deleteWhereIdEq(K id) {
    var opk = dsc.getPkField();
    return opk.map(mtFieldDescriptor -> deleteWhereEq(mtFieldDescriptor.getFieldName(), id)).orElse(-1L);
  }
}
