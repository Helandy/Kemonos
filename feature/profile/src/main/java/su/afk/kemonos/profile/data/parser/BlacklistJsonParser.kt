package su.afk.kemonos.profile.data.parser

import com.google.gson.JsonElement
import su.afk.kemonos.profile.domain.blacklist.IBlacklistJsonParser
import su.afk.kemonos.storage.api.repository.blacklist.BlacklistedAuthor
import javax.inject.Inject

internal class BlacklistJsonParser @Inject constructor() : IBlacklistJsonParser {

    override fun parse(rawJson: String): List<BlacklistedAuthor?> {
        val root = parseJsonArrayOrNull(rawJson) ?: error("Invalid blacklist import format")
        return root.map(::parseItem)
    }

    /** Accepts both new and legacy export fields and maps them to Room model. */
    private fun parseItem(element: JsonElement): BlacklistedAuthor? {
        if (!element.isJsonObject) return null
        val obj = element.asJsonObject

        val service = obj.stringField("service") ?: return null
        val creatorId = obj.stringField("creatorId") ?: obj.stringField("id") ?: return null
        val creatorName = obj.stringField("creatorName")
            ?: obj.stringField("name")
            ?: creatorId
        val createdAt = obj.longField("createdAt") ?: System.currentTimeMillis()

        return BlacklistedAuthor(
            service = service,
            creatorId = creatorId,
            creatorName = creatorName,
            createdAt = createdAt,
        )
    }
}
