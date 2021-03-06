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
public class UserDao extends MtWriteDao<io.vacco.metolithe.schema.User, java.lang.Integer> {
  
  public static final String pk_uid = "uid";
  
  public static final String fld_pw = "pw";
  public static final String fld_alias = "alias";
  public static final String fld_email = "email";
  public static final String fld_tid = "tid";
  public static final String fld_tagSignature = "tagSignature";
  
  public UserDao(String schema, MtCaseFormat fmt, FluentJdbc jdbc, MtIdFn<java.lang.Integer> idFn) {
    super(schema, jdbc, new MtDescriptor<>(io.vacco.metolithe.schema.User.class, fmt), idFn);
  }
  
  public Collection<io.vacco.metolithe.schema.User> loadWherePwEq(java.lang.String pw) {
    return loadWhereEq(fld_pw, pw);
  }

  public final Map<java.lang.String, List<io.vacco.metolithe.schema.User>> loadWherePwIn(java.lang.String ... values) {
    return loadWhereIn(fld_pw, values);
  }

  public long deleteWherePwEq(java.lang.String pw) {
    return deleteWhereEq(fld_pw, pw);
  }
  
  public Collection<io.vacco.metolithe.schema.User> loadWhereAliasEq(java.lang.String alias) {
    return loadWhereEq(fld_alias, alias);
  }

  public final Map<java.lang.String, List<io.vacco.metolithe.schema.User>> loadWhereAliasIn(java.lang.String ... values) {
    return loadWhereIn(fld_alias, values);
  }

  public long deleteWhereAliasEq(java.lang.String alias) {
    return deleteWhereEq(fld_alias, alias);
  }
  
  public Collection<io.vacco.metolithe.schema.User> loadWhereEmailEq(java.lang.String email) {
    return loadWhereEq(fld_email, email);
  }

  public final Map<java.lang.String, List<io.vacco.metolithe.schema.User>> loadWhereEmailIn(java.lang.String ... values) {
    return loadWhereIn(fld_email, values);
  }

  public long deleteWhereEmailEq(java.lang.String email) {
    return deleteWhereEq(fld_email, email);
  }
  
  public Collection<io.vacco.metolithe.schema.User> loadWhereTidEq(java.lang.Long tid) {
    return loadWhereEq(fld_tid, tid);
  }

  public final Map<java.lang.Long, List<io.vacco.metolithe.schema.User>> loadWhereTidIn(java.lang.Long ... values) {
    return loadWhereIn(fld_tid, values);
  }

  public long deleteWhereTidEq(java.lang.Long tid) {
    return deleteWhereEq(fld_tid, tid);
  }
  
  public Collection<io.vacco.metolithe.schema.User> loadWhereTagSignatureEq(java.lang.String tagSignature) {
    return loadWhereEq(fld_tagSignature, tagSignature);
  }

  public final Map<java.lang.String, List<io.vacco.metolithe.schema.User>> loadWhereTagSignatureIn(java.lang.String ... values) {
    return loadWhereIn(fld_tagSignature, values);
  }

  public long deleteWhereTagSignatureEq(java.lang.String tagSignature) {
    return deleteWhereEq(fld_tagSignature, tagSignature);
  }
  
}
