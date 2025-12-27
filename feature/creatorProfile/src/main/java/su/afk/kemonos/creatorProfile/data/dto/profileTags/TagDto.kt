package su.afk.kemonos.creatorProfile.data.dto.profileTags


import com.google.gson.annotations.SerializedName
import su.afk.kemonos.domain.domain.models.Tag

internal data class TagDto(
    @SerializedName("post_count")
    val postCount: Int,
    @SerializedName("tag")
    val tag: String
) {
    companion object {
        fun TagDto.toDomain(): Tag = Tag(
            tag = tag,
            postCount = postCount
        )

        fun List<TagDto>.toDomain(): List<Tag> = map { it.toDomain() }
    }
}