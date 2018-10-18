package io.vacco.mt.dao

import io.vacco.metolithe.core.BaseUpdateDao
import io.vacco.metolithe.core.EntityDescriptor
import io.vacco.metolithe.core.Murmur3LongGenerator
import io.vacco.mt.schema.BlogMetadata
import org.codejargon.fluentjdbc.api.FluentJdbc

class BlogMetadataDao(jdbc: FluentJdbc, sourceSchema: String) :
    BaseUpdateDao<BlogMetadata, Long>(
            BlogMetadata::class.java, jdbc, sourceSchema,
            EntityDescriptor.CaseFormat.KEEP_CASE, Murmur3LongGenerator())
