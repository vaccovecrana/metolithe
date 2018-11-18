package io.vacco.mt.dao;

import io.vacco.metolithe.core.*;
import io.vacco.mt.schema.valid.Bus;
import org.codejargon.fluentjdbc.api.FluentJdbc;

public class BusDao extends BaseDao<Bus, Long> {
  public BusDao(FluentJdbc jdbc, String sourceSchema) {
    super(jdbc, sourceSchema, new EntityDescriptor<>(
        Bus.class, EntityDescriptor.CaseFormat.KEEP_CASE, null
    ), new Murmur3LongGenerator());
  }
}
