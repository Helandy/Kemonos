package su.afk.kemonos.creatorProfile.data.dto.onlyhaven

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.creatorProfile.api.domain.models.profileSimilar.SimilarCreator
import java.time.Instant

/**
 * Похожий автор OnlyHaven.
 *
 * Эндпоинт называется /similar, а не /recommended как у kemono.
 * Даты первой индексации источник не отдаёт.
 */
data class OnlyHavenSimilarCreatorDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("displayName") val displayName: String? = null,
    @SerializedName("service") val service: String,

    /** unix-секунды */
    @SerializedName("updated") val updated: Long? = null,

    @SerializedName("score") val score: Double? = null,
) {
    companion object {
        fun OnlyHavenSimilarCreatorDto.toDomain(): SimilarCreator = SimilarCreator(
            id = id,
            indexed = "",
            name = displayName?.takeIf { it.isNotBlank() } ?: name,
            publicId = null,
            relationId = null,
            service = service,
            updated = updated?.takeIf { it > 0 }?.let { Instant.ofEpochSecond(it).toString() },
            score = score,
        )
    }
}

data class OnlyHavenSimilarPageDto(
    @SerializedName("creators") val creators: List<OnlyHavenSimilarCreatorDto>? = null,
)
