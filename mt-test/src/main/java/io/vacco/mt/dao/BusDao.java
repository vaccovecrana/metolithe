package io.vacco.mt.dao;

import io.vacco.metolithe.core.*;
import io.vacco.mt.schema.valid.Bus;
import org.codejargon.fluentjdbc.api.FluentJdbc;

public class BusDao extends BaseDao<Bus, Long> {
  public BusDao(FluentJdbc jdbc, String sourceSchema) {
    super(Bus.class, jdbc, sourceSchema,
        EntityDescriptor.CaseFormat.KEEP_CASE, new Murmur3LongGenerator());
  }
}
