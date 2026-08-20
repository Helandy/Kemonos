package su.afk.kemonos.data.dto.onlyhaven

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.data.dto.onlyhaven.OnlyHavenAttachmentDto.Companion.toDomainOrNull
import su.afk.kemonos.domain.models.PostDomain
import java.time.Instant

data class OnlyHavenSiblingDto(
    @SerializedName("id") val id: String?,
)

/**
 * Пост OnlyHaven: одна модель на ленту, список автора и деталь.
 *
 * Поля различаются по набору, а не по смыслу: в ленте есть creatorId/creatorName,
 * в списке автора их нет, в детали вместо captionHtml приходит сырой caption.
 */
data class OnlyHavenPostDto(
    @SerializedName("id") val id: String,
    @SerializedName("service") val service: String,

    @SerializedName("creatorId") val creatorId: String? = null,
    @SerializedName("creatorName") val creatorName: String? = null,

    @SerializedName("captionHtml") val captionHtml: String? = null,
    @SerializedName("caption") val caption: String? = null,

    /** unix-секунды */
    @SerializedName("added") val added: Long? = null,
    @SerializedName("published") val published: Long? = null,

    /** счётчик закладок, не булев флаг */
    @SerializedName("bookmarked") val bookmarked: Int? = null,

    @SerializedName("attachments") val attachments: List<OnlyHavenAttachmentDto>? = null,

    @SerializedName("prev") val prev: OnlyHavenSiblingDto? = null,
    @SerializedName("next") val next: OnlyHavenSiblingDto? = null,
) {
    companion object {

        private val HTML_TAG = Regex("<[^>]+>")

        /**
         * Отрывок для карточки поста у kemono — простой текст, а OnlyHaven
         * отдаёт только HTML. Полный текст остаётся размеченным: его рендерит UI.
         */
        private fun String.toExcerpt(): String =
            replace(Regex("(?i)<br[^>]*>|</p>"), " ")
                .replace(HTML_TAG, "")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace(Regex("\\s+"), " ")
                .trim()

        /** Приложение хранит даты ISO-строками, API отдаёт unix-секунды. */
        internal fun Long?.toIsoOrNull(): String? =
            this?.takeIf { it > 0 }?.let { Instant.ofEpochSecond(it).toString() }

        /**
         * @param creatorIdFallback id автора для маршрутов вида /{service}/user/{id}/posts,
         *        где сам ответ его не содержит.
         */
        fun OnlyHavenPostDto.toDomain(
            fileBaseUrl: String,
            creatorIdFallback: String? = null,
        ): PostDomain {
            val mapped = attachments.orEmpty().mapNotNull { it.toDomainOrNull(fileBaseUrl) }
            val body = caption ?: captionHtml

            return PostDomain(
                id = id,
                userId = creatorId ?: creatorIdFallback.orEmpty(),
                service = service,
                /** Заголовков у постов нет — в карточке показываем имя автора. */
                title = creatorName,
                content = body,
                substring = body?.toExcerpt(),
                added = added.toIsoOrNull(),
                published = published.toIsoOrNull(),
                edited = null,
                /**
                 * Отдельного «основного файла» у источника нет — всё лежит в attachments.
                 * Продублировать сюда первое вложение нельзя: счётчики считают file
                 * и attachments вместе, и одно видео превращалось в два.
                 */
                file = null,
                incompleteRewards = null,
                poll = null,
                attachments = mapped,
                tags = emptyList(),
                nextId = next?.id,
                prevId = prev?.id,
                favedSeq = null,
                favCount = bookmarked,
            )
        }
    }
}

/** Ответы лент: {total, posts[]} — total настоящий, в отличие от kemono. */
data class OnlyHavenPostsPageDto(
    @SerializedName("total") val total: Int? = null,
    @SerializedName("posts") val posts: List<OnlyHavenPostDto>? = null,
)
