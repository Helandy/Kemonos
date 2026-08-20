package su.afk.kemonos.creators.data.dto.onlyhaven

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.domain.models.creator.Creators
import su.afk.kemonos.domain.models.creator.CreatorsSort

data class OnlyHavenCreatorDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("displayName") val displayName: String? = null,
    @SerializedName("service") val service: String,

    /** unix-секунды */
    @SerializedName("indexed") val indexed: Long? = null,
    @SerializedName("updated") val updated: Long? = null,

    /** счётчик закладок, не булев флаг */
    @SerializedName("bookmarked") val bookmarked: Int? = null,

    @SerializedName("postCount") val postCount: Int? = null,
    @SerializedName("videoCount") val videoCount: Int? = null,
    @SerializedName("imageCount") val imageCount: Int? = null,
) {
    companion object {
        fun OnlyHavenCreatorDto.toDomain(): Creators = Creators(
            favorited = bookmarked ?: 0,
            id = id,
            indexed = indexed?.toInt() ?: 0,
            name = displayName?.takeIf { it.isNotBlank() } ?: name,
            service = service,
            updated = updated?.toInt() ?: 0,
        )

        /** Значения параметра `sort` из спеки OnlyHaven. */
        fun CreatorsSort.toOnlyHavenSort(): String = when (this) {
            CreatorsSort.POPULARITY -> "bookmarked"
            CreatorsSort.INDEX -> "indexed"
            CreatorsSort.UPDATE -> "updated"
            CreatorsSort.NAME -> "name"
        }
    }
}

data class OnlyHavenCreatorsPageDto(
    @SerializedName("total") val total: Int? = null,
    @SerializedName("creators") val creators: List<OnlyHavenCreatorDto>? = null,
)
