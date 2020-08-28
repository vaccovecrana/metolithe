package io.vacco.mt.dao;

import io.vacco.metolithe.base.Murmur3IntGenerator;
import io.vacco.metolithe.core.*;
import io.vacco.mt.schema.valid.Bus;
import org.codejargon.fluentjdbc.api.FluentJdbc;

public class BusDao extends BaseDao<Bus, Integer> {
  public BusDao(FluentJdbc jdbc, String sourceSchema) {
    super(jdbc, sourceSchema, new EntityDescriptor<>(
        Bus.class, EntityDescriptor.CaseFormat.KEEP_CASE
    ), new Murmur3IntGenerator());
  }
}
