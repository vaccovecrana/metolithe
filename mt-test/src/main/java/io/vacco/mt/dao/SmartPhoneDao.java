package io.vacco.mt.dao;

import io.vacco.metolithe.core.BaseUpdateDao;
import io.vacco.metolithe.core.EntityDescriptor;
import io.vacco.mt.schema.SmartPhone;
import org.codejargon.fluentjdbc.api.FluentJdbc;

public class SmartPhoneDao extends BaseUpdateDao<SmartPhone> {
  public SmartPhoneDao(FluentJdbc jdbc, String sourceSchema) {
    super(SmartPhone.class, jdbc, sourceSchema, EntityDescriptor.CaseFormat.LOWER_CASE);
  }
}
