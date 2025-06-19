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
public class UserFollowDao extends MtWriteDao<io.vacco.mt.test.schema.UserFollow, java.lang.Integer> {

  public static final String fld_fid = "fid";
  public static final String fld_fromUid = "fromUid";
  public static final String fld_toUid = "toUid";
  
  public UserFollowDao(String schema, MtCaseFormat fmt, MtJdbc jdbc, MtIdFn<java.lang.Integer> idFn) {
    super(schema, jdbc, new MtDescriptor<>(io.vacco.mt.test.schema.UserFollow.class, fmt), idFn);
  }
  
  public MtFieldDescriptor fld_fid() {
    return this.dsc.getField(fld_fid);
  }

  public List<io.vacco.mt.test.schema.UserFollow> loadWhereFidEq(java.lang.Integer fid) {
    return loadWhereEq(fld_fid, fid);
  }

  public final Map<java.lang.Integer, List<io.vacco.mt.test.schema.UserFollow>> loadWhereFidIn(java.lang.Integer ... values) {
    return loadWhereIn(fld_fid, values);
  }

  public MtResult<io.vacco.mt.test.schema.UserFollow> deleteWhereFidEq(java.lang.Integer fid) {
    return deleteWhereEq(fld_fid, fid);
  }
  
  public MtFieldDescriptor fld_fromUid() {
    return this.dsc.getField(fld_fromUid);
  }

  public List<io.vacco.mt.test.schema.UserFollow> loadWhereFromUidEq(java.lang.Integer fromUid) {
    return loadWhereEq(fld_fromUid, fromUid);
  }

  public final Map<java.lang.Integer, List<io.vacco.mt.test.schema.UserFollow>> loadWhereFromUidIn(java.lang.Integer ... values) {
    return loadWhereIn(fld_fromUid, values);
  }

  public MtResult<io.vacco.mt.test.schema.UserFollow> deleteWhereFromUidEq(java.lang.Integer fromUid) {
    return deleteWhereEq(fld_fromUid, fromUid);
  }
  
  public MtFieldDescriptor fld_toUid() {
    return this.dsc.getField(fld_toUid);
  }

  public List<io.vacco.mt.test.schema.UserFollow> loadWhereToUidEq(java.lang.Integer toUid) {
    return loadWhereEq(fld_toUid, toUid);
  }

  public final Map<java.lang.Integer, List<io.vacco.mt.test.schema.UserFollow>> loadWhereToUidIn(java.lang.Integer ... values) {
    return loadWhereIn(fld_toUid, values);
  }

  public MtResult<io.vacco.mt.test.schema.UserFollow> deleteWhereToUidEq(java.lang.Integer toUid) {
    return deleteWhereEq(fld_toUid, toUid);
  }
  
}
