package io.vacco.mt.test.dao;

import io.vacco.metolithe.core.MtCaseFormat;
import io.vacco.metolithe.core.MtDescriptor;
import io.vacco.metolithe.core.MtFieldDescriptor;
import io.vacco.metolithe.core.MtIdFn;
import io.vacco.metolithe.core.MtWriteDao;

import org.codejargon.fluentjdbc.api.FluentJdbc;

import java.util.List;
import java.util.Map;

/**************************************************
 * Generated source file. Do not modify directly. *
 **************************************************/
public class KeyNamespaceDao extends MtWriteDao<io.vacco.mt.test.schema.KeyNamespace, java.lang.Integer> {

  public static final String fld_id = "id";
  public static final String fld_kid = "kid";
  public static final String fld_nsId = "nsId";
  
  public KeyNamespaceDao(String schema, MtCaseFormat fmt, FluentJdbc jdbc, MtIdFn<java.lang.Integer> idFn) {
    super(schema, jdbc, new MtDescriptor<>(io.vacco.mt.test.schema.KeyNamespace.class, fmt), idFn);
  }
  
  public MtFieldDescriptor fld_id() {
    return this.dsc.getField(fld_id);
  }

  public List<io.vacco.mt.test.schema.KeyNamespace> loadWhereIdEq(java.lang.Integer id) {
    return loadWhereEq(fld_id, id);
  }

  public final Map<java.lang.Integer, List<io.vacco.mt.test.schema.KeyNamespace>> loadWhereIdIn(java.lang.Integer ... values) {
    return loadWhereIn(fld_id, values);
  }

  public long deleteWhereIdEq(java.lang.Integer id) {
    return deleteWhereEq(fld_id, id);
  }
  
  public MtFieldDescriptor fld_kid() {
    return this.dsc.getField(fld_kid);
  }

  public List<io.vacco.mt.test.schema.KeyNamespace> loadWhereKidEq(java.lang.Integer kid) {
    return loadWhereEq(fld_kid, kid);
  }

  public final Map<java.lang.Integer, List<io.vacco.mt.test.schema.KeyNamespace>> loadWhereKidIn(java.lang.Integer ... values) {
    return loadWhereIn(fld_kid, values);
  }

  public long deleteWhereKidEq(java.lang.Integer kid) {
    return deleteWhereEq(fld_kid, kid);
  }
  
  public MtFieldDescriptor fld_nsId() {
    return this.dsc.getField(fld_nsId);
  }

  public List<io.vacco.mt.test.schema.KeyNamespace> loadWhereNsIdEq(java.lang.Integer nsId) {
    return loadWhereEq(fld_nsId, nsId);
  }

  public final Map<java.lang.Integer, List<io.vacco.mt.test.schema.KeyNamespace>> loadWhereNsIdIn(java.lang.Integer ... values) {
    return loadWhereIn(fld_nsId, values);
  }

  public long deleteWhereNsIdEq(java.lang.Integer nsId) {
    return deleteWhereEq(fld_nsId, nsId);
  }
  
}
