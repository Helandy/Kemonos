package su.afk.kemonos.creatorProfile.data.dto.onlyhaven

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.domain.models.Profile
import java.time.Instant

/**
 * Профиль автора OnlyHaven.
 *
 * Счётчиков постов/DM в самом профиле нет — они приходят в строке списка
 * авторов, поэтому здесь остаются пустыми.
 */
data class OnlyHavenProfileDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("displayName") val displayName: String? = null,
    @SerializedName("service") val service: String,

    /** unix-секунды */
    @SerializedName("indexed") val indexed: Long? = null,
    @SerializedName("updated") val updated: Long? = null,

    @SerializedName("bookmarked") val bookmarked: Int? = null,
) {
    companion object {
        private fun Long?.toIsoOrNull(): String? =
            this?.takeIf { it > 0 }?.let { Instant.ofEpochSecond(it).toString() }

        fun OnlyHavenProfileDto.toDomain(): Profile = Profile(
            /** Личные сообщения у источника есть, но отдельного признака в профиле нет. */
            hasChats = null,
            id = id,
            service = service,
            name = displayName?.takeIf { it.isNotBlank() } ?: name,
            indexed = indexed.toIsoOrNull(),
            publicId = null,
            relationId = null,
            updated = updated.toIsoOrNull(),
            postCount = null,
            dmCount = null,
            shareCount = null,
            chatCount = null,
        )
    }
}
