package io.vacco.mt.dao;

import io.vacco.metolithe.base.Murmur3IntGenerator;
import io.vacco.metolithe.core.*;
import io.vacco.mt.schema.valid.BlogTag;
import org.codejargon.fluentjdbc.api.FluentJdbc;

public class BlogTagDao extends BaseUpdateDao<BlogTag, Integer> {
  public BlogTagDao(FluentJdbc jdbc, String sourceSchema) {
    super(jdbc, sourceSchema, new EntityDescriptor<>(
        BlogTag.class, EntityDescriptor.CaseFormat.KEEP_CASE),
        new Murmur3IntGenerator());
  }
}
