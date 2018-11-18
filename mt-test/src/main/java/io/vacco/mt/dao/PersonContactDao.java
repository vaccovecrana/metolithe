package io.vacco.mt.dao;

import io.vacco.metolithe.core.BaseUpdateDao;
import io.vacco.metolithe.core.EntityDescriptor;
import io.vacco.metolithe.core.Murmur3LongGenerator;
import io.vacco.mt.schema.valid.PersonContact;
import org.codejargon.fluentjdbc.api.FluentJdbc;

public class PersonContactDao extends BaseUpdateDao<PersonContact, Long> {
  public PersonContactDao(FluentJdbc jdbc, String sourceSchema) {
    super(jdbc, sourceSchema, new EntityDescriptor<>(
        PersonContact.class, EntityDescriptor.CaseFormat.UPPER_CASE, null
    ), new Murmur3LongGenerator());
  }
}
