package su.afk.kemonos.posts.data.dto.tags

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.api.domain.tags.Tags

internal data class TagsDto(
    @SerializedName("tag")
    val tags: String?,
    @SerializedName("post_count")
    val count: Int?,
) {
    companion object {
        fun TagsDto.toDomain() = Tags(
            tags = this.tags,
            count = this.count,
        )
    }
}