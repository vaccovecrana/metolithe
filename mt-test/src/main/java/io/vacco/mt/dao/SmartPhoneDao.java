package io.vacco.mt.dao;

import io.vacco.metolithe.core.BaseUpdateDao;
import io.vacco.metolithe.spi.MtCodec;
import io.vacco.mt.schema.SmartPhone;
import org.codejargon.fluentjdbc.api.FluentJdbc;

import java.util.Arrays;
import java.util.Collection;

public class SmartPhoneDao extends BaseUpdateDao<SmartPhone> {

  public SmartPhoneDao(FluentJdbc jdbc, MtCodec codec, String sourceSchema) {
    super(jdbc, codec, sourceSchema);
  }

  @Override
  protected Class<SmartPhone> getTargetClass() { return SmartPhone.class; }

  @Override
  protected Collection<Class<? extends Enum>> getEnumClasses() {
    return Arrays.asList(
        SmartPhone.BatteryType.class,
        SmartPhone.Feature.class,
        SmartPhone.Os.class);
  }
}
