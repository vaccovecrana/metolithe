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
public class DbUserRoleDao extends MtWriteDao<io.vacco.mt.test.schema.DbUserRole, java.lang.String> {

  public static final String fld_rid = "rid";
  public static final String fld_createdUtcMs = "createdUtcMs";
  
  public DbUserRoleDao(String schema, MtCaseFormat fmt, FluentJdbc jdbc, MtIdFn<java.lang.String> idFn) {
    super(schema, jdbc, new MtDescriptor<>(io.vacco.mt.test.schema.DbUserRole.class, fmt), idFn);
  }
  
  public MtFieldDescriptor fld_rid() {
    return this.dsc.getField(fld_rid);
  }

  public List<io.vacco.mt.test.schema.DbUserRole> loadWhereRidEq(java.lang.String rid) {
    return loadWhereEq(fld_rid, rid);
  }

  public final Map<java.lang.String, List<io.vacco.mt.test.schema.DbUserRole>> loadWhereRidIn(java.lang.String ... values) {
    return loadWhereIn(fld_rid, values);
  }

  public long deleteWhereRidEq(java.lang.String rid) {
    return deleteWhereEq(fld_rid, rid);
  }
  
  public MtFieldDescriptor fld_createdUtcMs() {
    return this.dsc.getField(fld_createdUtcMs);
  }

  public List<io.vacco.mt.test.schema.DbUserRole> loadWhereCreatedUtcMsEq(java.lang.Long createdUtcMs) {
    return loadWhereEq(fld_createdUtcMs, createdUtcMs);
  }

  public final Map<java.lang.Long, List<io.vacco.mt.test.schema.DbUserRole>> loadWhereCreatedUtcMsIn(java.lang.Long ... values) {
    return loadWhereIn(fld_createdUtcMs, values);
  }

  public long deleteWhereCreatedUtcMsEq(java.lang.Long createdUtcMs) {
    return deleteWhereEq(fld_createdUtcMs, createdUtcMs);
  }
  
}
