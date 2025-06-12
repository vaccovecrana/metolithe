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
public class ApiKeyDao extends MtWriteDao<io.vacco.mt.test.schema.ApiKey, java.lang.Integer> {

  public static final String fld_kid = "kid";
  public static final String fld_pKid = "pKid";
  public static final String fld_uid = "uid";
  public static final String fld_name = "name";
  public static final String fld_hash = "hash";
  
  public ApiKeyDao(String schema, MtCaseFormat fmt, MtJdbc jdbc, MtIdFn<java.lang.Integer> idFn) {
    super(schema, jdbc, new MtDescriptor<>(io.vacco.mt.test.schema.ApiKey.class, fmt), idFn);
  }
  
  public MtFieldDescriptor fld_kid() {
    return this.dsc.getField(fld_kid);
  }

  public List<io.vacco.mt.test.schema.ApiKey> loadWhereKidEq(java.lang.Integer kid) {
    return loadWhereEq(fld_kid, kid);
  }

  public final Map<java.lang.Integer, List<io.vacco.mt.test.schema.ApiKey>> loadWhereKidIn(java.lang.Integer ... values) {
    return loadWhereIn(fld_kid, values);
  }

  public MtResult<io.vacco.mt.test.schema.ApiKey> deleteWhereKidEq(java.lang.Integer kid) {
    return deleteWhereEq(fld_kid, kid);
  }
  
  public MtFieldDescriptor fld_pKid() {
    return this.dsc.getField(fld_pKid);
  }

  public List<io.vacco.mt.test.schema.ApiKey> loadWherePKidEq(java.lang.Integer pKid) {
    return loadWhereEq(fld_pKid, pKid);
  }

  public final Map<java.lang.Integer, List<io.vacco.mt.test.schema.ApiKey>> loadWherePKidIn(java.lang.Integer ... values) {
    return loadWhereIn(fld_pKid, values);
  }

  public MtResult<io.vacco.mt.test.schema.ApiKey> deleteWherePKidEq(java.lang.Integer pKid) {
    return deleteWhereEq(fld_pKid, pKid);
  }
  
  public MtFieldDescriptor fld_uid() {
    return this.dsc.getField(fld_uid);
  }

  public List<io.vacco.mt.test.schema.ApiKey> loadWhereUidEq(java.lang.Integer uid) {
    return loadWhereEq(fld_uid, uid);
  }

  public final Map<java.lang.Integer, List<io.vacco.mt.test.schema.ApiKey>> loadWhereUidIn(java.lang.Integer ... values) {
    return loadWhereIn(fld_uid, values);
  }

  public MtResult<io.vacco.mt.test.schema.ApiKey> deleteWhereUidEq(java.lang.Integer uid) {
    return deleteWhereEq(fld_uid, uid);
  }
  
  public MtFieldDescriptor fld_name() {
    return this.dsc.getField(fld_name);
  }

  public List<io.vacco.mt.test.schema.ApiKey> loadWhereNameEq(java.lang.String name) {
    return loadWhereEq(fld_name, name);
  }

  public final Map<java.lang.String, List<io.vacco.mt.test.schema.ApiKey>> loadWhereNameIn(java.lang.String ... values) {
    return loadWhereIn(fld_name, values);
  }

  public MtResult<io.vacco.mt.test.schema.ApiKey> deleteWhereNameEq(java.lang.String name) {
    return deleteWhereEq(fld_name, name);
  }
  
  public MtFieldDescriptor fld_hash() {
    return this.dsc.getField(fld_hash);
  }

  public List<io.vacco.mt.test.schema.ApiKey> loadWhereHashEq(java.lang.String hash) {
    return loadWhereEq(fld_hash, hash);
  }

  public final Map<java.lang.String, List<io.vacco.mt.test.schema.ApiKey>> loadWhereHashIn(java.lang.String ... values) {
    return loadWhereIn(fld_hash, values);
  }

  public MtResult<io.vacco.mt.test.schema.ApiKey> deleteWhereHashEq(java.lang.String hash) {
    return deleteWhereEq(fld_hash, hash);
  }
  
}
