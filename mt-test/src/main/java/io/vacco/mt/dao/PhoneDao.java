package io.vacco.mt.dao;

import io.vacco.metolithe.core.BaseUpdateDao;
import io.vacco.metolithe.core.EntityDescriptor;
import io.vacco.metolithe.core.Murmur3LongGenerator;
import io.vacco.mt.schema.Phone;
import org.codejargon.fluentjdbc.api.FluentJdbc;

public class PhoneDao extends BaseUpdateDao<Phone, Long> {
  public PhoneDao(FluentJdbc jdbc, String sourceSchema) {
    super(Phone.class, jdbc, sourceSchema,
        EntityDescriptor.CaseFormat.UPPER_CASE, new Murmur3LongGenerator());
  }
}
