package su.afk.kemonos.profile.domain.blacklist

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import su.afk.kemonos.preferences.domainResolver.IDomainResolver
import su.afk.kemonos.preferences.domainResolver.selectedSiteByService
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.site.setSiteAndAwait
import su.afk.kemonos.storage.api.repository.blacklist.BlacklistedAuthor
import su.afk.kemonos.storage.api.repository.blacklist.IStoreBlacklistedAuthorsRepository
import javax.inject.Inject

internal enum class BlacklistImportEntryStatus {
    SUCCESS,
    FAILED,
    SKIPPED,
}

internal enum class BlacklistImportEntryReason {
    NONE,
    INVALID_ITEM,
    DUPLICATE_IN_FILE,
    REQUEST_FAILED,
}

internal data class BlacklistImportEntry(
    val rowNumber: Int,
    val target: String,
    val status: BlacklistImportEntryStatus,
    val reason: BlacklistImportEntryReason,
)

internal data class BlacklistImportResult(
    val entries: List<BlacklistImportEntry>,
) {
    val processedCount: Int get() = entries.size
    val importedCount: Int get() = entries.count { it.status == BlacklistImportEntryStatus.SUCCESS }
    val failedCount: Int get() = entries.count { it.status == BlacklistImportEntryStatus.FAILED }
    val skippedCount: Int get() = entries.count { it.status == BlacklistImportEntryStatus.SKIPPED }
}

internal class ImportBlacklistFromJsonUseCase @Inject constructor(
    private val blacklistedAuthorsRepository: IStoreBlacklistedAuthorsRepository,
    private val domainResolver: IDomainResolver,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
) {

    /**
     * Imports blacklist entries from JSON array and returns per-row status.
     * Switches selected site before each upsert according to row service.
     */
    suspend operator fun invoke(rawJson: String): BlacklistImportResult {
        val root = JsonParser.parseString(rawJson)
        if (!root.isJsonArray) error("Invalid blacklist import format")

        val unique = LinkedHashMap<String, IndexedBlacklistAuthor>()
        val entries = ArrayList<BlacklistImportEntry>(root.asJsonArray.size())

        for ((index, element) in root.asJsonArray.withIndex()) {
            val rowNumber = index + 1
            val parsed = parseBlacklistItem(element)
            if (parsed == null) {
                entries += BlacklistImportEntry(
                    rowNumber = rowNumber,
                    target = "",
                    status = BlacklistImportEntryStatus.SKIPPED,
                    reason = BlacklistImportEntryReason.INVALID_ITEM,
                )
                continue
            }

            val key = "${parsed.service}:${parsed.creatorId}"
            if (unique.containsKey(key)) {
                entries += BlacklistImportEntry(
                    rowNumber = rowNumber,
                    target = key,
                    status = BlacklistImportEntryStatus.SKIPPED,
                    reason = BlacklistImportEntryReason.DUPLICATE_IN_FILE,
                )
                continue
            }

            unique[key] = IndexedBlacklistAuthor(
                rowNumber = rowNumber,
                author = parsed,
            )
        }

        for ((key, indexedAuthor) in unique.entries) {
            val targetSite = domainResolver.selectedSiteByService(indexedAuthor.author.service)
            selectedSiteUseCase.setSiteAndAwait(targetSite)

            runCatching { blacklistedAuthorsRepository.upsert(indexedAuthor.author) }
                .onSuccess {
                    entries += BlacklistImportEntry(
                        rowNumber = indexedAuthor.rowNumber,
                        target = key,
                        status = BlacklistImportEntryStatus.SUCCESS,
                        reason = BlacklistImportEntryReason.NONE,
                    )
                }
                .onFailure {
                    entries += BlacklistImportEntry(
                        rowNumber = indexedAuthor.rowNumber,
                        target = key,
                        status = BlacklistImportEntryStatus.FAILED,
                        reason = BlacklistImportEntryReason.REQUEST_FAILED,
                    )
                }
        }

        return BlacklistImportResult(entries = entries.sortedBy { it.rowNumber })
    }

    /** Accepts both new and legacy export fields and maps them to Room model. */
    private fun parseBlacklistItem(element: JsonElement): BlacklistedAuthor? {
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

    private fun JsonObject.stringField(name: String): String? =
        runCatching { get(name) }
            .getOrNull()
            ?.takeIf { !it.isJsonNull }
            ?.let { runCatching { it.asString }.getOrNull() }
            ?.trim()
            ?.takeIf { it.isNotEmpty() }

    private fun JsonObject.longField(name: String): Long? =
        runCatching { get(name) }
            .getOrNull()
            ?.takeIf { !it.isJsonNull }
            ?.let { runCatching { it.asLong }.getOrNull() }

    private data class IndexedBlacklistAuthor(
        val rowNumber: Int,
        val author: BlacklistedAuthor,
    )
}
