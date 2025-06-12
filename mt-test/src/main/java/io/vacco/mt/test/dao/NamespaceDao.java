package io.vacco.mt.test.dao;

import io.vacco.metolithe.core.MtCaseFormat;
import io.vacco.metolithe.core.MtDescriptor;
import io.vacco.metolithe.core.MtFieldDescriptor;
import io.vacco.metolithe.id.MtIdFn;
import io.vacco.metolithe.dao.MtWriteDao;
import io.vacco.metolithe.query.MtJdbc;
import io.vacco.metolithe.query.MtResult;

import java.util.List;
import java.util.Map;

/**************************************************
 * Generated source file. Do not modify directly. *
 **************************************************/
public class NamespaceDao extends MtWriteDao<io.vacco.mt.test.schema.Namespace, java.lang.Integer> {

  public static final String fld_nsId = "nsId";
  public static final String fld_name = "name";
  public static final String fld_path = "path";
  public static final String fld_createdAtUtcMs = "createdAtUtcMs";
  
  public NamespaceDao(String schema, MtCaseFormat fmt, MtJdbc jdbc, MtIdFn<java.lang.Integer> idFn) {
    super(schema, jdbc, new MtDescriptor<>(io.vacco.mt.test.schema.Namespace.class, fmt), idFn);
  }
  
  public MtFieldDescriptor fld_nsId() {
    return this.dsc.getField(fld_nsId);
  }

  public List<io.vacco.mt.test.schema.Namespace> loadWhereNsIdEq(java.lang.Integer nsId) {
    return loadWhereEq(fld_nsId, nsId);
  }

  public final Map<java.lang.Integer, List<io.vacco.mt.test.schema.Namespace>> loadWhereNsIdIn(java.lang.Integer ... values) {
    return loadWhereIn(fld_nsId, values);
  }

  public MtResult<io.vacco.mt.test.schema.Namespace> deleteWhereNsIdEq(java.lang.Integer nsId) {
    return deleteWhereEq(fld_nsId, nsId);
  }
  
  public MtFieldDescriptor fld_name() {
    return this.dsc.getField(fld_name);
  }

  public List<io.vacco.mt.test.schema.Namespace> loadWhereNameEq(java.lang.String name) {
    return loadWhereEq(fld_name, name);
  }

  public final Map<java.lang.String, List<io.vacco.mt.test.schema.Namespace>> loadWhereNameIn(java.lang.String ... values) {
    return loadWhereIn(fld_name, values);
  }

  public MtResult<io.vacco.mt.test.schema.Namespace> deleteWhereNameEq(java.lang.String name) {
    return deleteWhereEq(fld_name, name);
  }
  
  public MtFieldDescriptor fld_path() {
    return this.dsc.getField(fld_path);
  }

  public List<io.vacco.mt.test.schema.Namespace> loadWherePathEq(java.lang.String path) {
    return loadWhereEq(fld_path, path);
  }

  public final Map<java.lang.String, List<io.vacco.mt.test.schema.Namespace>> loadWherePathIn(java.lang.String ... values) {
    return loadWhereIn(fld_path, values);
  }

  public MtResult<io.vacco.mt.test.schema.Namespace> deleteWherePathEq(java.lang.String path) {
    return deleteWhereEq(fld_path, path);
  }
  
  public MtFieldDescriptor fld_createdAtUtcMs() {
    return this.dsc.getField(fld_createdAtUtcMs);
  }

  public List<io.vacco.mt.test.schema.Namespace> loadWhereCreatedAtUtcMsEq(java.lang.Long createdAtUtcMs) {
    return loadWhereEq(fld_createdAtUtcMs, createdAtUtcMs);
  }

  public final Map<java.lang.Long, List<io.vacco.mt.test.schema.Namespace>> loadWhereCreatedAtUtcMsIn(java.lang.Long ... values) {
    return loadWhereIn(fld_createdAtUtcMs, values);
  }

  public MtResult<io.vacco.mt.test.schema.Namespace> deleteWhereCreatedAtUtcMsEq(java.lang.Long createdAtUtcMs) {
    return deleteWhereEq(fld_createdAtUtcMs, createdAtUtcMs);
  }
  
}
