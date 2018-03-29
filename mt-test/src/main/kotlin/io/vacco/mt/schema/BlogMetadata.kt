package io.vacco.mt.schema

import io.vacco.metolithe.annotations.MtAttribute
import io.vacco.metolithe.annotations.MtEntity
import io.vacco.metolithe.annotations.MtId

@MtEntity
data class BlogMetadata(
    @MtId() val id: String = "",
    @MtAttribute(nil = false, len = 150)
    val title: String = "",
    @MtAttribute(nil = false, len = 320)
    val tags: Set<String> = emptySet(),
    @MtAttribute(nil = false, len = 16)
    val status: PublishStatus = PublishStatus.SCHEDULED
) {
  enum class PublishStatus { PUBLISHED, SCHEDULED }
}