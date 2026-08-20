package su.afk.kemonos.posts.data.dto.onlyhaven

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.posts.api.dms.DmDomain

/**
 * Личное сообщение OnlyHaven.
 *
 * Дат у DM в API нет вовсе, поэтому added/published остаются пустыми,
 * а идентификатором служит id (у kemono на этом месте hash).
 */
data class OnlyHavenDmDto(
    @SerializedName("id") val id: String,
    @SerializedName("service") val service: String,
    @SerializedName("creatorId") val creatorId: String? = null,
    @SerializedName("creatorName") val creatorName: String? = null,
    @SerializedName("contentHtml") val contentHtml: String? = null,
    @SerializedName("bookmarked") val bookmarked: Int? = null,
) {
    companion object {
        fun OnlyHavenDmDto.toDomain(): DmDomain = DmDomain(
            hash = id,
            service = service,
            user = creatorId.orEmpty(),
            content = contentHtml.orEmpty(),
            added = "",
            published = "",
            artistId = creatorId.orEmpty(),
            artistName = creatorName.orEmpty(),
            artistUpdated = "",
        )
    }
}

data class OnlyHavenDmsPageDto(
    @SerializedName("total") val total: Int? = null,
    @SerializedName("dms") val dms: List<OnlyHavenDmDto>? = null,
)
