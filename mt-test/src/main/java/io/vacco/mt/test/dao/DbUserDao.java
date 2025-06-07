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
public class DbUserDao extends MtWriteDao<io.vacco.mt.test.schema.DbUser, java.lang.Integer> {

  public static final String fld_uid = "uid";
  public static final String fld_pw = "pw";
  public static final String fld_alias = "alias";
  public static final String fld_email = "email";
  public static final String fld_tid = "tid";
  public static final String fld_tagSignature = "tagSignature";
  public static final String fld_rid = "rid";
  
  public DbUserDao(String schema, MtCaseFormat fmt, FluentJdbc jdbc, MtIdFn<java.lang.Integer> idFn) {
    super(schema, jdbc, new MtDescriptor<>(io.vacco.mt.test.schema.DbUser.class, fmt), idFn);
  }
  
  public MtFieldDescriptor fld_uid() {
    return this.dsc.getField(fld_uid);
  }

  public List<io.vacco.mt.test.schema.DbUser> loadWhereUidEq(java.lang.Integer uid) {
    return loadWhereEq(fld_uid, uid);
  }

  public final Map<java.lang.Integer, List<io.vacco.mt.test.schema.DbUser>> loadWhereUidIn(java.lang.Integer ... values) {
    return loadWhereIn(fld_uid, values);
  }

  public long deleteWhereUidEq(java.lang.Integer uid) {
    return deleteWhereEq(fld_uid, uid);
  }
  
  public MtFieldDescriptor fld_pw() {
    return this.dsc.getField(fld_pw);
  }

  public List<io.vacco.mt.test.schema.DbUser> loadWherePwEq(java.lang.String pw) {
    return loadWhereEq(fld_pw, pw);
  }

  public final Map<java.lang.String, List<io.vacco.mt.test.schema.DbUser>> loadWherePwIn(java.lang.String ... values) {
    return loadWhereIn(fld_pw, values);
  }

  public long deleteWherePwEq(java.lang.String pw) {
    return deleteWhereEq(fld_pw, pw);
  }
  
  public MtFieldDescriptor fld_alias() {
    return this.dsc.getField(fld_alias);
  }

  public List<io.vacco.mt.test.schema.DbUser> loadWhereAliasEq(java.lang.String alias) {
    return loadWhereEq(fld_alias, alias);
  }

  public final Map<java.lang.String, List<io.vacco.mt.test.schema.DbUser>> loadWhereAliasIn(java.lang.String ... values) {
    return loadWhereIn(fld_alias, values);
  }

  public long deleteWhereAliasEq(java.lang.String alias) {
    return deleteWhereEq(fld_alias, alias);
  }
  
  public MtFieldDescriptor fld_email() {
    return this.dsc.getField(fld_email);
  }

  public List<io.vacco.mt.test.schema.DbUser> loadWhereEmailEq(java.lang.String email) {
    return loadWhereEq(fld_email, email);
  }

  public final Map<java.lang.String, List<io.vacco.mt.test.schema.DbUser>> loadWhereEmailIn(java.lang.String ... values) {
    return loadWhereIn(fld_email, values);
  }

  public long deleteWhereEmailEq(java.lang.String email) {
    return deleteWhereEq(fld_email, email);
  }
  
  public MtFieldDescriptor fld_tid() {
    return this.dsc.getField(fld_tid);
  }

  public List<io.vacco.mt.test.schema.DbUser> loadWhereTidEq(java.lang.Long tid) {
    return loadWhereEq(fld_tid, tid);
  }

  public final Map<java.lang.Long, List<io.vacco.mt.test.schema.DbUser>> loadWhereTidIn(java.lang.Long ... values) {
    return loadWhereIn(fld_tid, values);
  }

  public long deleteWhereTidEq(java.lang.Long tid) {
    return deleteWhereEq(fld_tid, tid);
  }
  
  public MtFieldDescriptor fld_tagSignature() {
    return this.dsc.getField(fld_tagSignature);
  }

  public List<io.vacco.mt.test.schema.DbUser> loadWhereTagSignatureEq(java.lang.String tagSignature) {
    return loadWhereEq(fld_tagSignature, tagSignature);
  }

  public final Map<java.lang.String, List<io.vacco.mt.test.schema.DbUser>> loadWhereTagSignatureIn(java.lang.String ... values) {
    return loadWhereIn(fld_tagSignature, values);
  }

  public long deleteWhereTagSignatureEq(java.lang.String tagSignature) {
    return deleteWhereEq(fld_tagSignature, tagSignature);
  }
  
  public MtFieldDescriptor fld_rid() {
    return this.dsc.getField(fld_rid);
  }

  public List<io.vacco.mt.test.schema.DbUser> loadWhereRidEq(java.lang.String rid) {
    return loadWhereEq(fld_rid, rid);
  }

  public final Map<java.lang.String, List<io.vacco.mt.test.schema.DbUser>> loadWhereRidIn(java.lang.String ... values) {
    return loadWhereIn(fld_rid, values);
  }

  public long deleteWhereRidEq(java.lang.String rid) {
    return deleteWhereEq(fld_rid, rid);
  }
  
}
