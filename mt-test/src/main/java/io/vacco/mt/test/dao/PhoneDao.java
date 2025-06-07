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
public class PhoneDao extends MtWriteDao<io.vacco.mt.test.schema.Phone, java.lang.Integer> {

  public static final String fld_pid = "pid";
  public static final String fld_countryCode = "countryCode";
  public static final String fld_number = "number";
  public static final String fld_smsVerificationCode = "smsVerificationCode";
  
  public PhoneDao(String schema, MtCaseFormat fmt, FluentJdbc jdbc, MtIdFn<java.lang.Integer> idFn) {
    super(schema, jdbc, new MtDescriptor<>(io.vacco.mt.test.schema.Phone.class, fmt), idFn);
  }
  
  public MtFieldDescriptor fld_pid() {
    return this.dsc.getField(fld_pid);
  }

  public List<io.vacco.mt.test.schema.Phone> loadWherePidEq(java.lang.Integer pid) {
    return loadWhereEq(fld_pid, pid);
  }

  public final Map<java.lang.Integer, List<io.vacco.mt.test.schema.Phone>> loadWherePidIn(java.lang.Integer ... values) {
    return loadWhereIn(fld_pid, values);
  }

  public long deleteWherePidEq(java.lang.Integer pid) {
    return deleteWhereEq(fld_pid, pid);
  }
  
  public MtFieldDescriptor fld_countryCode() {
    return this.dsc.getField(fld_countryCode);
  }

  public List<io.vacco.mt.test.schema.Phone> loadWhereCountryCodeEq(java.lang.Integer countryCode) {
    return loadWhereEq(fld_countryCode, countryCode);
  }

  public final Map<java.lang.Integer, List<io.vacco.mt.test.schema.Phone>> loadWhereCountryCodeIn(java.lang.Integer ... values) {
    return loadWhereIn(fld_countryCode, values);
  }

  public long deleteWhereCountryCodeEq(java.lang.Integer countryCode) {
    return deleteWhereEq(fld_countryCode, countryCode);
  }
  
  public MtFieldDescriptor fld_number() {
    return this.dsc.getField(fld_number);
  }

  public List<io.vacco.mt.test.schema.Phone> loadWhereNumberEq(java.lang.String number) {
    return loadWhereEq(fld_number, number);
  }

  public final Map<java.lang.String, List<io.vacco.mt.test.schema.Phone>> loadWhereNumberIn(java.lang.String ... values) {
    return loadWhereIn(fld_number, values);
  }

  public long deleteWhereNumberEq(java.lang.String number) {
    return deleteWhereEq(fld_number, number);
  }
  
  public MtFieldDescriptor fld_smsVerificationCode() {
    return this.dsc.getField(fld_smsVerificationCode);
  }

  public List<io.vacco.mt.test.schema.Phone> loadWhereSmsVerificationCodeEq(java.lang.Integer smsVerificationCode) {
    return loadWhereEq(fld_smsVerificationCode, smsVerificationCode);
  }

  public final Map<java.lang.Integer, List<io.vacco.mt.test.schema.Phone>> loadWhereSmsVerificationCodeIn(java.lang.Integer ... values) {
    return loadWhereIn(fld_smsVerificationCode, values);
  }

  public long deleteWhereSmsVerificationCodeEq(java.lang.Integer smsVerificationCode) {
    return deleteWhereEq(fld_smsVerificationCode, smsVerificationCode);
  }
  
}
