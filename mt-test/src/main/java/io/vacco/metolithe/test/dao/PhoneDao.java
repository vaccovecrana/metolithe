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
public class PhoneDao extends MtWriteDao<io.vacco.metolithe.schema.Phone, java.lang.Integer> {
  
  public static final String pk_pid = "pid";
  
  public static final String fld_countryCode = "countryCode";
  public static final String fld_number = "number";
  public static final String fld_smsVerificationCode = "smsVerificationCode";
  
  public PhoneDao(String schema, MtCaseFormat fmt, FluentJdbc jdbc, MtIdFn<java.lang.Integer> idFn) {
    super(schema, jdbc, new MtDescriptor<>(io.vacco.metolithe.schema.Phone.class, fmt), idFn);
  }
  
  public Collection<io.vacco.metolithe.schema.Phone> loadWhereCountryCodeEq(java.lang.Integer countryCode) {
    return loadWhereEq(fld_countryCode, countryCode);
  }

  public final Map<java.lang.Integer, List<io.vacco.metolithe.schema.Phone>> loadWhereCountryCodeIn(java.lang.Integer ... values) {
    return loadWhereIn(fld_countryCode, values);
  }

  public long deleteWhereCountryCodeEq(java.lang.Integer countryCode) {
    return deleteWhereEq(fld_countryCode, countryCode);
  }
  
  public Collection<io.vacco.metolithe.schema.Phone> loadWhereNumberEq(java.lang.String number) {
    return loadWhereEq(fld_number, number);
  }

  public final Map<java.lang.String, List<io.vacco.metolithe.schema.Phone>> loadWhereNumberIn(java.lang.String ... values) {
    return loadWhereIn(fld_number, values);
  }

  public long deleteWhereNumberEq(java.lang.String number) {
    return deleteWhereEq(fld_number, number);
  }
  
  public Collection<io.vacco.metolithe.schema.Phone> loadWhereSmsVerificationCodeEq(java.lang.Integer smsVerificationCode) {
    return loadWhereEq(fld_smsVerificationCode, smsVerificationCode);
  }

  public final Map<java.lang.Integer, List<io.vacco.metolithe.schema.Phone>> loadWhereSmsVerificationCodeIn(java.lang.Integer ... values) {
    return loadWhereIn(fld_smsVerificationCode, values);
  }

  public long deleteWhereSmsVerificationCodeEq(java.lang.Integer smsVerificationCode) {
    return deleteWhereEq(fld_smsVerificationCode, smsVerificationCode);
  }
  
}
