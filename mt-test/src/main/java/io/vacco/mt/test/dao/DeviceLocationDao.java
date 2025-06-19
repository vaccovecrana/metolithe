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
public class DeviceLocationDao extends MtWriteDao<io.vacco.mt.test.schema.DeviceLocation, java.lang.Long> {

  public static final String fld_did = "did";
  public static final String fld_uid = "uid";
  public static final String fld_geoHash2 = "geoHash2";
  public static final String fld_geoHash4 = "geoHash4";
  public static final String fld_geoHash12 = "geoHash12";
  public static final String fld_geoHash12Ip = "geoHash12Ip";
  public static final String fld_logTimeUtcMs = "logTimeUtcMs";
  public static final String fld_fraudScore = "fraudScore";
  public static final String fld_fraudScoreDelta = "fraudScoreDelta";
  
  public DeviceLocationDao(String schema, MtCaseFormat fmt, MtJdbc jdbc, MtIdFn<java.lang.Long> idFn) {
    super(schema, jdbc, new MtDescriptor<>(io.vacco.mt.test.schema.DeviceLocation.class, fmt), idFn);
  }
  
  public MtFieldDescriptor fld_did() {
    return this.dsc.getField(fld_did);
  }

  public List<io.vacco.mt.test.schema.DeviceLocation> loadWhereDidEq(java.lang.Long did) {
    return loadWhereEq(fld_did, did);
  }

  public final Map<java.lang.Long, List<io.vacco.mt.test.schema.DeviceLocation>> loadWhereDidIn(java.lang.Long ... values) {
    return loadWhereIn(fld_did, values);
  }

  public MtResult<io.vacco.mt.test.schema.DeviceLocation> deleteWhereDidEq(java.lang.Long did) {
    return deleteWhereEq(fld_did, did);
  }
  
  public MtFieldDescriptor fld_uid() {
    return this.dsc.getField(fld_uid);
  }

  public List<io.vacco.mt.test.schema.DeviceLocation> loadWhereUidEq(java.lang.Integer uid) {
    return loadWhereEq(fld_uid, uid);
  }

  public final Map<java.lang.Integer, List<io.vacco.mt.test.schema.DeviceLocation>> loadWhereUidIn(java.lang.Integer ... values) {
    return loadWhereIn(fld_uid, values);
  }

  public MtResult<io.vacco.mt.test.schema.DeviceLocation> deleteWhereUidEq(java.lang.Integer uid) {
    return deleteWhereEq(fld_uid, uid);
  }
  
  public MtFieldDescriptor fld_geoHash2() {
    return this.dsc.getField(fld_geoHash2);
  }

  public List<io.vacco.mt.test.schema.DeviceLocation> loadWhereGeoHash2Eq(java.lang.String geoHash2) {
    return loadWhereEq(fld_geoHash2, geoHash2);
  }

  public final Map<java.lang.String, List<io.vacco.mt.test.schema.DeviceLocation>> loadWhereGeoHash2In(java.lang.String ... values) {
    return loadWhereIn(fld_geoHash2, values);
  }

  public MtResult<io.vacco.mt.test.schema.DeviceLocation> deleteWhereGeoHash2Eq(java.lang.String geoHash2) {
    return deleteWhereEq(fld_geoHash2, geoHash2);
  }
  
  public MtFieldDescriptor fld_geoHash4() {
    return this.dsc.getField(fld_geoHash4);
  }

  public List<io.vacco.mt.test.schema.DeviceLocation> loadWhereGeoHash4Eq(java.lang.String geoHash4) {
    return loadWhereEq(fld_geoHash4, geoHash4);
  }

  public final Map<java.lang.String, List<io.vacco.mt.test.schema.DeviceLocation>> loadWhereGeoHash4In(java.lang.String ... values) {
    return loadWhereIn(fld_geoHash4, values);
  }

  public MtResult<io.vacco.mt.test.schema.DeviceLocation> deleteWhereGeoHash4Eq(java.lang.String geoHash4) {
    return deleteWhereEq(fld_geoHash4, geoHash4);
  }
  
  public MtFieldDescriptor fld_geoHash12() {
    return this.dsc.getField(fld_geoHash12);
  }

  public List<io.vacco.mt.test.schema.DeviceLocation> loadWhereGeoHash12Eq(java.lang.String geoHash12) {
    return loadWhereEq(fld_geoHash12, geoHash12);
  }

  public final Map<java.lang.String, List<io.vacco.mt.test.schema.DeviceLocation>> loadWhereGeoHash12In(java.lang.String ... values) {
    return loadWhereIn(fld_geoHash12, values);
  }

  public MtResult<io.vacco.mt.test.schema.DeviceLocation> deleteWhereGeoHash12Eq(java.lang.String geoHash12) {
    return deleteWhereEq(fld_geoHash12, geoHash12);
  }
  
  public MtFieldDescriptor fld_geoHash12Ip() {
    return this.dsc.getField(fld_geoHash12Ip);
  }

  public List<io.vacco.mt.test.schema.DeviceLocation> loadWhereGeoHash12IpEq(java.lang.String geoHash12Ip) {
    return loadWhereEq(fld_geoHash12Ip, geoHash12Ip);
  }

  public final Map<java.lang.String, List<io.vacco.mt.test.schema.DeviceLocation>> loadWhereGeoHash12IpIn(java.lang.String ... values) {
    return loadWhereIn(fld_geoHash12Ip, values);
  }

  public MtResult<io.vacco.mt.test.schema.DeviceLocation> deleteWhereGeoHash12IpEq(java.lang.String geoHash12Ip) {
    return deleteWhereEq(fld_geoHash12Ip, geoHash12Ip);
  }
  
  public MtFieldDescriptor fld_logTimeUtcMs() {
    return this.dsc.getField(fld_logTimeUtcMs);
  }

  public List<io.vacco.mt.test.schema.DeviceLocation> loadWhereLogTimeUtcMsEq(java.lang.Long logTimeUtcMs) {
    return loadWhereEq(fld_logTimeUtcMs, logTimeUtcMs);
  }

  public final Map<java.lang.Long, List<io.vacco.mt.test.schema.DeviceLocation>> loadWhereLogTimeUtcMsIn(java.lang.Long ... values) {
    return loadWhereIn(fld_logTimeUtcMs, values);
  }

  public MtResult<io.vacco.mt.test.schema.DeviceLocation> deleteWhereLogTimeUtcMsEq(java.lang.Long logTimeUtcMs) {
    return deleteWhereEq(fld_logTimeUtcMs, logTimeUtcMs);
  }
  
  public MtFieldDescriptor fld_fraudScore() {
    return this.dsc.getField(fld_fraudScore);
  }

  public List<io.vacco.mt.test.schema.DeviceLocation> loadWhereFraudScoreEq(java.lang.Double fraudScore) {
    return loadWhereEq(fld_fraudScore, fraudScore);
  }

  public final Map<java.lang.Double, List<io.vacco.mt.test.schema.DeviceLocation>> loadWhereFraudScoreIn(java.lang.Double ... values) {
    return loadWhereIn(fld_fraudScore, values);
  }

  public MtResult<io.vacco.mt.test.schema.DeviceLocation> deleteWhereFraudScoreEq(java.lang.Double fraudScore) {
    return deleteWhereEq(fld_fraudScore, fraudScore);
  }
  
  public MtFieldDescriptor fld_fraudScoreDelta() {
    return this.dsc.getField(fld_fraudScoreDelta);
  }

  public List<io.vacco.mt.test.schema.DeviceLocation> loadWhereFraudScoreDeltaEq(java.lang.Float fraudScoreDelta) {
    return loadWhereEq(fld_fraudScoreDelta, fraudScoreDelta);
  }

  public final Map<java.lang.Float, List<io.vacco.mt.test.schema.DeviceLocation>> loadWhereFraudScoreDeltaIn(java.lang.Float ... values) {
    return loadWhereIn(fld_fraudScoreDelta, values);
  }

  public MtResult<io.vacco.mt.test.schema.DeviceLocation> deleteWhereFraudScoreDeltaEq(java.lang.Float fraudScoreDelta) {
    return deleteWhereEq(fld_fraudScoreDelta, fraudScoreDelta);
  }
  
}
