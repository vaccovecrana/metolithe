package io.vacco.mt.dao;

import io.vacco.metolithe.core.BaseUpdateDao;
import io.vacco.metolithe.core.EntityDescriptor;
import io.vacco.metolithe.base.Murmur3LongGenerator;
import io.vacco.mt.schema.valid.SmartPhone;
import org.codejargon.fluentjdbc.api.FluentJdbc;

public class SmartPhoneDao extends BaseUpdateDao<SmartPhone, Long> {
  public SmartPhoneDao(FluentJdbc jdbc, String sourceSchema) {
    super(jdbc, sourceSchema, new EntityDescriptor<>(
        SmartPhone.class, EntityDescriptor.CaseFormat.LOWER_CASE),
        new Murmur3LongGenerator()
    );
  }
}
