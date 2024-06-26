package io.vacco.metolithe.test.dao;

import io.vacco.metolithe.core.MtCaseFormat;
import io.vacco.metolithe.core.MtDescriptor;
import io.vacco.metolithe.core.MtIdFn;
import io.vacco.metolithe.core.MtWriteDao;

import org.codejargon.fluentjdbc.api.FluentJdbc;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**************************************************
 * Generated source file. Do not modify directly. *
 **************************************************/
public class DbUserDao extends MtWriteDao<io.vacco.metolithe.schema.DbUser, java.lang.Integer> {

  public static final String fld_uid = "uid";
  public static final String fld_pw = "pw";
  public static final String fld_alias = "alias";
  public static final String fld_email = "email";
  public static final String fld_tid = "tid";
  public static final String fld_tagSignature = "tagSignature";
  public static final String fld_rid = "rid";
  
  public DbUserDao(String schema, MtCaseFormat fmt, FluentJdbc jdbc, MtIdFn<java.lang.Integer> idFn) {
    super(schema, jdbc, new MtDescriptor<>(io.vacco.metolithe.schema.DbUser.class, fmt), idFn);
  }
  
  public Collection<io.vacco.metolithe.schema.DbUser> loadWhereUidEq(java.lang.Integer uid) {
    return loadWhereEq(fld_uid, uid);
  }

  public final Map<java.lang.Integer, List<io.vacco.metolithe.schema.DbUser>> loadWhereUidIn(java.lang.Integer ... values) {
    return loadWhereIn(fld_uid, values);
  }

  public long deleteWhereUidEq(java.lang.Integer uid) {
    return deleteWhereEq(fld_uid, uid);
  }
  
  public Collection<io.vacco.metolithe.schema.DbUser> loadWherePwEq(java.lang.String pw) {
    return loadWhereEq(fld_pw, pw);
  }

  public final Map<java.lang.String, List<io.vacco.metolithe.schema.DbUser>> loadWherePwIn(java.lang.String ... values) {
    return loadWhereIn(fld_pw, values);
  }

  public long deleteWherePwEq(java.lang.String pw) {
    return deleteWhereEq(fld_pw, pw);
  }
  
  public Collection<io.vacco.metolithe.schema.DbUser> loadWhereAliasEq(java.lang.String alias) {
    return loadWhereEq(fld_alias, alias);
  }

  public final Map<java.lang.String, List<io.vacco.metolithe.schema.DbUser>> loadWhereAliasIn(java.lang.String ... values) {
    return loadWhereIn(fld_alias, values);
  }

  public long deleteWhereAliasEq(java.lang.String alias) {
    return deleteWhereEq(fld_alias, alias);
  }
  
  public Collection<io.vacco.metolithe.schema.DbUser> loadWhereEmailEq(java.lang.String email) {
    return loadWhereEq(fld_email, email);
  }

  public final Map<java.lang.String, List<io.vacco.metolithe.schema.DbUser>> loadWhereEmailIn(java.lang.String ... values) {
    return loadWhereIn(fld_email, values);
  }

  public long deleteWhereEmailEq(java.lang.String email) {
    return deleteWhereEq(fld_email, email);
  }
  
  public Collection<io.vacco.metolithe.schema.DbUser> loadWhereTidEq(java.lang.Long tid) {
    return loadWhereEq(fld_tid, tid);
  }

  public final Map<java.lang.Long, List<io.vacco.metolithe.schema.DbUser>> loadWhereTidIn(java.lang.Long ... values) {
    return loadWhereIn(fld_tid, values);
  }

  public long deleteWhereTidEq(java.lang.Long tid) {
    return deleteWhereEq(fld_tid, tid);
  }
  
  public Collection<io.vacco.metolithe.schema.DbUser> loadWhereTagSignatureEq(java.lang.String tagSignature) {
    return loadWhereEq(fld_tagSignature, tagSignature);
  }

  public final Map<java.lang.String, List<io.vacco.metolithe.schema.DbUser>> loadWhereTagSignatureIn(java.lang.String ... values) {
    return loadWhereIn(fld_tagSignature, values);
  }

  public long deleteWhereTagSignatureEq(java.lang.String tagSignature) {
    return deleteWhereEq(fld_tagSignature, tagSignature);
  }
  
  public Collection<io.vacco.metolithe.schema.DbUser> loadWhereRidEq(java.lang.String rid) {
    return loadWhereEq(fld_rid, rid);
  }

  public final Map<java.lang.String, List<io.vacco.metolithe.schema.DbUser>> loadWhereRidIn(java.lang.String ... values) {
    return loadWhereIn(fld_rid, values);
  }

  public long deleteWhereRidEq(java.lang.String rid) {
    return deleteWhereEq(fld_rid, rid);
  }
  
}
