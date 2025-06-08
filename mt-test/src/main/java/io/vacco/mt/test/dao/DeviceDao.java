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
public class DeviceDao extends MtWriteDao<io.vacco.mt.test.schema.Device, java.lang.Long> {

  public static final String fld_did = "did";
  public static final String fld_type = "type";
  public static final String fld_signingKey = "signingKey";
  
  public DeviceDao(String schema, MtCaseFormat fmt, FluentJdbc jdbc, MtIdFn<java.lang.Long> idFn) {
    super(schema, jdbc, new MtDescriptor<>(io.vacco.mt.test.schema.Device.class, fmt), idFn);
  }
  
  public MtFieldDescriptor fld_did() {
    return this.dsc.getField(fld_did);
  }

  public List<io.vacco.mt.test.schema.Device> loadWhereDidEq(java.lang.Long did) {
    return loadWhereEq(fld_did, did);
  }

  public final Map<java.lang.Long, List<io.vacco.mt.test.schema.Device>> loadWhereDidIn(java.lang.Long ... values) {
    return loadWhereIn(fld_did, values);
  }

  public long deleteWhereDidEq(java.lang.Long did) {
    return deleteWhereEq(fld_did, did);
  }
  
  public MtFieldDescriptor fld_type() {
    return this.dsc.getField(fld_type);
  }

  public List<io.vacco.mt.test.schema.Device> loadWhereTypeEq(io.vacco.mt.test.schema.Device.DType type) {
    return loadWhereEq(fld_type, type);
  }

  public final Map<io.vacco.mt.test.schema.Device.DType, List<io.vacco.mt.test.schema.Device>> loadWhereTypeIn(io.vacco.mt.test.schema.Device.DType ... values) {
    return loadWhereIn(fld_type, values);
  }

  public long deleteWhereTypeEq(io.vacco.mt.test.schema.Device.DType type) {
    return deleteWhereEq(fld_type, type);
  }
  
  public MtFieldDescriptor fld_signingKey() {
    return this.dsc.getField(fld_signingKey);
  }

  public List<io.vacco.mt.test.schema.Device> loadWhereSigningKeyEq(java.lang.String signingKey) {
    return loadWhereEq(fld_signingKey, signingKey);
  }

  public final Map<java.lang.String, List<io.vacco.mt.test.schema.Device>> loadWhereSigningKeyIn(java.lang.String ... values) {
    return loadWhereIn(fld_signingKey, values);
  }

  public long deleteWhereSigningKeyEq(java.lang.String signingKey) {
    return deleteWhereEq(fld_signingKey, signingKey);
  }
  
}
