package io.vacco.mt.schema.valid

import io.vacco.metolithe.annotations.MtAttribute
import io.vacco.metolithe.annotations.MtEntity
import io.vacco.metolithe.annotations.MtId
import io.vacco.metolithe.annotations.MtIdGroup

@MtEntity
data class BlogMetadata(
    @MtId var id: Long = 0,
    @MtIdGroup(number = 0, position = 0)
    @MtAttribute(nil = false, len = 150)
    val title: String = "",
    @MtAttribute(nil = false, len = 16)
    val status: PublishStatus = PublishStatus.SCHEDULED
) {
  enum class PublishStatus { PUBLISHED, SCHEDULED }
}