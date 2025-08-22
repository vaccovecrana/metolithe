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
public class DeviceTagDao extends MtWriteDao<io.vacco.mt.test.schema.DeviceTag, java.lang.Long> {

  public DeviceTagDao(String schema, MtCaseFormat fmt, MtJdbc jdbc, MtIdFn<java.lang.Long> idFn) {
    super(schema, jdbc, new MtDescriptor<>(io.vacco.mt.test.schema.DeviceTag.class, fmt), idFn);
  }

}