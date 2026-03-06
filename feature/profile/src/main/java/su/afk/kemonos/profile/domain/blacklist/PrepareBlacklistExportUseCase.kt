package su.afk.kemonos.profile.domain.blacklist

import su.afk.kemonos.storage.api.repository.blacklist.BlacklistedAuthor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

internal data class BlacklistExportPayload(
    val fileName: String,
    val json: String,
)

internal class PrepareBlacklistExportUseCase @Inject constructor() {

    /** Builds export payload (file name + JSON body) from blacklist rows. */
    operator fun invoke(items: List<BlacklistedAuthor>): BlacklistExportPayload {
        val datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy"))
        val fileName = "Blacklist_Authors_(${items.size})_${datePart}.json"
        return BlacklistExportPayload(
            fileName = fileName,
            json = buildExportJson(items),
        )
    }

    /** Serializes blacklist rows to a compact JSON array. */
    private fun buildExportJson(items: List<BlacklistedAuthor>): String {
        return buildString(items.size * 64 + 2) {
            append("[")
            items.forEachIndexed { index, item ->
                if (index > 0) append(",")
                append(
                    """
                    {"service":${item.service.toJsonString()},"creatorId":${item.creatorId.toJsonString()},"creatorName":${item.creatorName.toJsonString()},"createdAt":${item.createdAt}}
                    """.trimIndent()
                )
            }
            append("]")
        }
    }

    private fun String.toJsonString(): String = buildString(length + 2) {
        append('"')
        for (ch in this@toJsonString) {
            when (ch) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(ch)
            }
        }
        append('"')
    }
}
