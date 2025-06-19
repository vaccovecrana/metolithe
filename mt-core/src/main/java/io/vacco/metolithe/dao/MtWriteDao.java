package io.vacco.metolithe.dao;

import io.vacco.metolithe.core.*;
import io.vacco.metolithe.id.MtIdFn;
import io.vacco.metolithe.query.*;
import java.util.*;
import java.util.function.*;

import static io.vacco.metolithe.core.MtErr.*;
import static io.vacco.metolithe.query.MtResult.*;
import static io.vacco.metolithe.core.MtCaseFormat.*;
import static java.lang.String.format;

public class MtWriteDao<T, K> extends MtReadDao<T, K> {

  public MtWriteDao(String schemaName, MtJdbc jdbc, MtDescriptor<T> d, MtIdFn<K> idFn) {
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

  private MtResult<T> save(T rec, boolean later) {
    return withId(rec, (fd, pk) -> {
      var query = getQueryCache().computeIfAbsent("insert", k ->
        format("insert into %s (%s) values (%s)",
          getTableName(),
          propNamesCsv(dsc, true, ""),
          placeholderCsv(dsc, true)
        )
      );
      var upd = sql().update(query);
      dsc.forEach(true, rec, upd::param);
      return result(rec, later ? upd : upd.executeOn(jdbc));
    });
  }

  public MtResult<T> save(T rec) {
    return save(rec, false);
  }

  public MtResult<T> saveLater(T rec) {
    return save(rec, true);
  }

  private MtResult<T> update(T rec, boolean later) {
    return withId(rec, (fd, pk) -> {
      var queryAssignments = placeHolderAssignmentCsv(dsc, false);
      var query = getQueryCache().computeIfAbsent("update",
        k -> format("update %s set %s where %s = :%s", getTableName(), queryAssignments,
          fd.getFieldName(), fd.getFieldName())
      );
      var upd = sql().update(query);
      dsc.forEach(false, rec, upd::param);
      upd.param(fd.getFieldName(), pk);
      return result(rec, later ? upd : upd.executeOn(jdbc));
    });
  }

  public MtResult<T> update(T rec) {
    return update(rec, false);
  }

  public MtResult<T> updateLater(T rec) {
    return update(rec, true);
  }

  private MtResult<T> upsert(T rec, boolean later) {
    return withId(rec, (fd, pk) -> load(pk).isEmpty()
      ? save(rec, later)
      : update(rec, later));
  }

  public MtResult<T> upsert(T rec) {
    return upsert(rec, false);
  }

  public MtResult<T> upsertLater(T rec) {
    return upsert(rec, true);
  }

  private MtResult<T> delete(T rec, boolean later) {
    return withId(rec, (fd, pk) -> {
      var query = getQueryCache().computeIfAbsent("delete",
        k -> format("delete from %s where %s = :%s", getTableName(), fd.getFieldName(), fd.getFieldName())
      );
      var cmd = sql().update(query).param(fd.getFieldName(), pk);
      return result(rec, later ? cmd : cmd.executeOn(jdbc));
    });
  }

  public MtResult<T> delete(T rec) {
    return delete(rec, false);
  }

  public MtResult<T> deleteLater(T rec) {
    return delete(rec, true);
  }

  private MtResult<T> deleteWhereEq(String field, Object value, boolean later) {
    var fn = dsc.getFormat().of(field);
    var query = getQueryCache().computeIfAbsent("deleteWhereEq" + fn,
      k -> format("delete from %s where %s = :%s", getTableName(), fn, fn)
    );
    var cmd = sql().update(query).param(fn, value);
    return result(null, later ? cmd : cmd.executeOn(jdbc));
  }

  public MtResult<T> deleteWhereEq(String field, Object value) {
    return deleteWhereEq(field, value, false);
  }

  public MtResult<T> deleteWhereEqLater(String field, Object value) {
    return deleteWhereEq(field, value, true);
  }

  private MtResult<T> deleteWhereIdEq(K id, boolean later) {
    return dsc.getPkField()
      .map(mtFieldDescriptor -> deleteWhereEq(mtFieldDescriptor.getFieldName(), id, later))
      .orElseThrow(() -> generalError(format("Type [%s] has no primary key", dsc)));
  }

  public MtResult<T> deleteWhereIdEq(K id) {
    return deleteWhereIdEq(id, false);
  }

  public MtResult<T> deleteWhereIdEqLater(K id) {
    return deleteWhereIdEq(id, true);
  }

}
