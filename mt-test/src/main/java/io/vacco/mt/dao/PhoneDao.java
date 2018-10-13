package io.vacco.mt.dao;

import io.vacco.metolithe.core.BaseUpdateDao;
import io.vacco.metolithe.core.EntityDescriptor;
import io.vacco.mt.schema.Phone;
import org.codejargon.fluentjdbc.api.FluentJdbc;

import java.util.ArrayList;
import java.util.Collection;

public class PhoneDao extends BaseUpdateDao<Phone> {
  public PhoneDao(FluentJdbc jdbc, String sourceSchema) {
    super(Phone.class, jdbc, sourceSchema, EntityDescriptor.CaseFormat.UPPER_CASE);
  }
}
