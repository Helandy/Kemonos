package su.afk.kemonos.posts.data.dto.onlyhaven

import com.google.gson.annotations.SerializedName
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Entities
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
        /**
         * У kemono в этом поле простой текст, а OnlyHaven отдаёт HTML —
         * иначе теги видны пользователю как есть.
         */
        private fun String.htmlToPlainText(): String {
            val settings = Document.OutputSettings()
                .prettyPrint(false)
                .escapeMode(Entities.EscapeMode.xhtml)
            val withBreaks = replace(Regex("(?i)<br[^>]*>|</p>"), "\n")
            return Jsoup.clean(withBreaks, "", org.jsoup.safety.Safelist.none(), settings)
                .let(Jsoup::parse)
                .text()
                .trim()
        }

        fun OnlyHavenDmDto.toDomain(): DmDomain = DmDomain(
            hash = id,
            service = service,
            user = creatorId.orEmpty(),
            content = contentHtml?.htmlToPlainText().orEmpty(),
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
