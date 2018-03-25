package io.vacco.mt.schema

import io.vacco.metolithe.annotations.MtAttribute
import io.vacco.metolithe.annotations.MtEntity
import io.vacco.metolithe.annotations.MtId
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@MtEntity
data class BlogMetadata(
    @MtId @Size(max = 8) val id: String = "",
    @MtAttribute @NotNull @Size(min = 8, max = 150)
    val title: String = "",
    @MtAttribute(maxByteLength = 320) @NotNull
    val tags: Set<String> = emptySet(),
    @MtAttribute(maxByteLength = 16) @NotNull
    val status: PublishStatus = PublishStatus.SCHEDULED
) {
  enum class PublishStatus { PUBLISHED, SCHEDULED }
}