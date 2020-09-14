package io.vacco.metolithe.test.dao;

import io.vacco.metolithe.core.MtCaseFormat;
import io.vacco.metolithe.core.MtDescriptor;
import io.vacco.metolithe.core.MtIdFn;
import io.vacco.metolithe.core.MtWriteDao;

import org.codejargon.fluentjdbc.api.FluentJdbc;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UserDao extends MtWriteDao<io.vacco.metolithe.schema.User, java.lang.Integer> {
  
  public static final String uid = "uid";
  
  public static final String pw = "pw";
  public static final String alias = "alias";
  public static final String email = "email";
  public static final String tid = "tid";
  public static final String tagSignature = "tagSignature";
  
  public UserDao(String schema, MtCaseFormat fmt, FluentJdbc jdbc, MtIdFn<java.lang.Integer> idFn) {
    super(schema, jdbc, new MtDescriptor<>(io.vacco.metolithe.schema.User.class, fmt), idFn);
  }
  
  public Collection<io.vacco.metolithe.schema.User> loadWherePwEq(java.lang.String pw) {
    return loadWhereEq("pw", pw);
  }

  public final Map<java.lang.String, List<io.vacco.metolithe.schema.User>> loadWherePwIn(java.lang.String ... values) {
    return loadWhereIn("pw", values);
  }

  public long deleteWherePwEq(java.lang.String pw) {
    return deleteWhereEq("pw", pw);
  }
  
  public Collection<io.vacco.metolithe.schema.User> loadWhereAliasEq(java.lang.String alias) {
    return loadWhereEq("alias", alias);
  }

  public final Map<java.lang.String, List<io.vacco.metolithe.schema.User>> loadWhereAliasIn(java.lang.String ... values) {
    return loadWhereIn("alias", values);
  }

  public long deleteWhereAliasEq(java.lang.String alias) {
    return deleteWhereEq("alias", alias);
  }
  
  public Collection<io.vacco.metolithe.schema.User> loadWhereEmailEq(java.lang.String email) {
    return loadWhereEq("email", email);
  }

  public final Map<java.lang.String, List<io.vacco.metolithe.schema.User>> loadWhereEmailIn(java.lang.String ... values) {
    return loadWhereIn("email", values);
  }

  public long deleteWhereEmailEq(java.lang.String email) {
    return deleteWhereEq("email", email);
  }
  
  public Collection<io.vacco.metolithe.schema.User> loadWhereTidEq(java.lang.Long tid) {
    return loadWhereEq("tid", tid);
  }

  public final Map<java.lang.Long, List<io.vacco.metolithe.schema.User>> loadWhereTidIn(java.lang.Long ... values) {
    return loadWhereIn("tid", values);
  }

  public long deleteWhereTidEq(java.lang.Long tid) {
    return deleteWhereEq("tid", tid);
  }
  
  public Collection<io.vacco.metolithe.schema.User> loadWhereTagSignatureEq(java.lang.String tagSignature) {
    return loadWhereEq("tagSignature", tagSignature);
  }

  public final Map<java.lang.String, List<io.vacco.metolithe.schema.User>> loadWhereTagSignatureIn(java.lang.String ... values) {
    return loadWhereIn("tagSignature", values);
  }

  public long deleteWhereTagSignatureEq(java.lang.String tagSignature) {
    return deleteWhereEq("tagSignature", tagSignature);
  }
  
}
