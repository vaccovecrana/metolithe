package io.vacco.mt.dao;

import io.vacco.metolithe.core.BaseUpdateDao;
import io.vacco.metolithe.core.EntityDescriptor;
import io.vacco.metolithe.spi.MtCodec;
import io.vacco.mt.schema.Phone;
import org.codejargon.fluentjdbc.api.FluentJdbc;

import java.util.ArrayList;
import java.util.Collection;

public class PhoneDao extends BaseUpdateDao<Phone> {
  public PhoneDao(FluentJdbc jdbc, MtCodec codec, String sourceSchema) {
    super(jdbc, codec, sourceSchema, EntityDescriptor.CaseFormat.UPPER_CASE);
  }
  @Override protected Class<Phone> getTargetClass() { return Phone.class; }
  @Override protected Collection<Class<? extends Enum>> getEnumClasses() { return new ArrayList<>(); }
}
