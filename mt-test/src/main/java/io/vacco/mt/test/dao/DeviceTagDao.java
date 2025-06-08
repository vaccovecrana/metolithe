package io.vacco.mt.test.dao;

import io.vacco.metolithe.core.MtCaseFormat;
import io.vacco.metolithe.core.MtDescriptor;
import io.vacco.metolithe.core.MtFieldDescriptor;
import io.vacco.metolithe.id.MtIdFn;
import io.vacco.metolithe.dao.MtWriteDao;

import org.codejargon.fluentjdbc.api.FluentJdbc;

import java.util.List;
import java.util.Map;

/**************************************************
 * Generated source file. Do not modify directly. *
 **************************************************/
public class DeviceTagDao extends MtWriteDao<io.vacco.mt.test.schema.DeviceTag, java.lang.Long> {

  public static final String fld_tid = "tid";
  public static final String fld_pid = "pid";
  public static final String fld_did = "did";
  public static final String fld_claimTimeUtcMs = "claimTimeUtcMs";
  public static final String fld_smsCodeSignature = "smsCodeSignature";
  
  public DeviceTagDao(String schema, MtCaseFormat fmt, FluentJdbc jdbc, MtIdFn<java.lang.Long> idFn) {
    super(schema, jdbc, new MtDescriptor<>(io.vacco.mt.test.schema.DeviceTag.class, fmt), idFn);
  }
  
  public MtFieldDescriptor fld_tid() {
    return this.dsc.getField(fld_tid);
  }

  public List<io.vacco.mt.test.schema.DeviceTag> loadWhereTidEq(java.lang.Long tid) {
    return loadWhereEq(fld_tid, tid);
  }

  public final Map<java.lang.Long, List<io.vacco.mt.test.schema.DeviceTag>> loadWhereTidIn(java.lang.Long ... values) {
    return loadWhereIn(fld_tid, values);
  }

  public long deleteWhereTidEq(java.lang.Long tid) {
    return deleteWhereEq(fld_tid, tid);
  }
  
  public MtFieldDescriptor fld_pid() {
    return this.dsc.getField(fld_pid);
  }

  public List<io.vacco.mt.test.schema.DeviceTag> loadWherePidEq(java.lang.Integer pid) {
    return loadWhereEq(fld_pid, pid);
  }

  public final Map<java.lang.Integer, List<io.vacco.mt.test.schema.DeviceTag>> loadWherePidIn(java.lang.Integer ... values) {
    return loadWhereIn(fld_pid, values);
  }

  public long deleteWherePidEq(java.lang.Integer pid) {
    return deleteWhereEq(fld_pid, pid);
  }
  
  public MtFieldDescriptor fld_did() {
    return this.dsc.getField(fld_did);
  }

  public List<io.vacco.mt.test.schema.DeviceTag> loadWhereDidEq(java.lang.Long did) {
    return loadWhereEq(fld_did, did);
  }

  public final Map<java.lang.Long, List<io.vacco.mt.test.schema.DeviceTag>> loadWhereDidIn(java.lang.Long ... values) {
    return loadWhereIn(fld_did, values);
  }

  public long deleteWhereDidEq(java.lang.Long did) {
    return deleteWhereEq(fld_did, did);
  }
  
  public MtFieldDescriptor fld_claimTimeUtcMs() {
    return this.dsc.getField(fld_claimTimeUtcMs);
  }

  public List<io.vacco.mt.test.schema.DeviceTag> loadWhereClaimTimeUtcMsEq(java.lang.Long claimTimeUtcMs) {
    return loadWhereEq(fld_claimTimeUtcMs, claimTimeUtcMs);
  }

  public final Map<java.lang.Long, List<io.vacco.mt.test.schema.DeviceTag>> loadWhereClaimTimeUtcMsIn(java.lang.Long ... values) {
    return loadWhereIn(fld_claimTimeUtcMs, values);
  }

  public long deleteWhereClaimTimeUtcMsEq(java.lang.Long claimTimeUtcMs) {
    return deleteWhereEq(fld_claimTimeUtcMs, claimTimeUtcMs);
  }
  
  public MtFieldDescriptor fld_smsCodeSignature() {
    return this.dsc.getField(fld_smsCodeSignature);
  }

  public List<io.vacco.mt.test.schema.DeviceTag> loadWhereSmsCodeSignatureEq(java.lang.String smsCodeSignature) {
    return loadWhereEq(fld_smsCodeSignature, smsCodeSignature);
  }

  public final Map<java.lang.String, List<io.vacco.mt.test.schema.DeviceTag>> loadWhereSmsCodeSignatureIn(java.lang.String ... values) {
    return loadWhereIn(fld_smsCodeSignature, values);
  }

  public long deleteWhereSmsCodeSignatureEq(java.lang.String smsCodeSignature) {
    return deleteWhereEq(fld_smsCodeSignature, smsCodeSignature);
  }
  
}
