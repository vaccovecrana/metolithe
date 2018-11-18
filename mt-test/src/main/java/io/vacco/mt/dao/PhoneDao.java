package io.vacco.mt.dao;

import io.vacco.metolithe.core.BaseUpdateDao;
import io.vacco.metolithe.core.EntityDescriptor;
import io.vacco.metolithe.core.Murmur3LongGenerator;
import io.vacco.mt.schema.valid.Phone;
import org.codejargon.fluentjdbc.api.FluentJdbc;

public class PhoneDao extends BaseUpdateDao<Phone, Long> {
  public PhoneDao(FluentJdbc jdbc, String sourceSchema) {
    super(jdbc, sourceSchema, new EntityDescriptor<>(
        Phone.class, EntityDescriptor.CaseFormat.UPPER_CASE, null
    ), new Murmur3LongGenerator());
  }
}
