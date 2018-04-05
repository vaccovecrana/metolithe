package io.vacco.mt.dao

import io.vacco.metolithe.core.BaseUpdateDao
import io.vacco.metolithe.core.EntityDescriptor
import io.vacco.metolithe.spi.MtCodec
import io.vacco.mt.schema.BlogMetadata
import org.codejargon.fluentjdbc.api.FluentJdbc

class BlogMetadataDao(jdbc: FluentJdbc, codec: MtCodec, sourceSchema: String) :
    BaseUpdateDao<BlogMetadata>(jdbc, codec, sourceSchema, EntityDescriptor.CaseFormat.KEEP_CASE) {
  override fun getTargetClass(): Class<BlogMetadata> = BlogMetadata::class.java
  override fun getEnumClasses(): Collection<Class<out Enum<*>>> =
      setOf<Class<out Enum<*>>>(BlogMetadata.PublishStatus::class.java)
}
