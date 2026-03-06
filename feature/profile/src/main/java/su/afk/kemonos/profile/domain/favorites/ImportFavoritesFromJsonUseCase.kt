package su.afk.kemonos.profile.domain.favorites

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.site.withSite
import javax.inject.Inject

internal enum class FavoritesImportType {
    ARTISTS,
    POSTS,
}

internal enum class FavoritesImportEntryStatus {
    SUCCESS,
    FAILED,
    SKIPPED,
}

internal enum class FavoritesImportEntryReason {
    NONE,
    INVALID_ITEM,
    DUPLICATE_IN_FILE,
    REQUEST_FAILED,
}

internal data class FavoritesImportEntry(
    val rowNumber: Int,
    val target: String,
    val status: FavoritesImportEntryStatus,
    val reason: FavoritesImportEntryReason,
)

internal data class FavoritesImportResult(
    val entries: List<FavoritesImportEntry>,
) {
    val processedCount: Int get() = entries.size
    val importedCount: Int get() = entries.count { it.status == FavoritesImportEntryStatus.SUCCESS }
    val failedCount: Int get() = entries.count { it.status == FavoritesImportEntryStatus.FAILED }
    val skippedCount: Int get() = entries.count { it.status == FavoritesImportEntryStatus.SKIPPED }
}

internal class ImportFavoritesFromJsonUseCase @Inject constructor(
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    private val favoritesRepository: IFavoritesRepository,
    private val importExportRepository: IImportExportRepository,
) {

    suspend operator fun invoke(
        site: SelectedSite,
        type: FavoritesImportType,
        rawJson: String,
    ): FavoritesImportResult {
        return when (type) {
            FavoritesImportType.ARTISTS -> importArtists(site = site, rawJson = rawJson)
            FavoritesImportType.POSTS -> importPosts(site = site, rawJson = rawJson)
        }
    }

    private suspend fun importArtists(
        site: SelectedSite,
        rawJson: String,
    ): FavoritesImportResult {
        val root = parseArrayOrThrow(rawJson)
        val seen = HashSet<String>(root.size())
        val resultEntries = ArrayList<FavoritesImportEntry>(root.size())

        selectedSiteUseCase.withSite(site) {
            root.forEachIndexed { index, element ->
                val rowNumber = index + 1
                val parsed = parseArtistItem(element)
                if (parsed == null) {
                    resultEntries += FavoritesImportEntry(
                        rowNumber = rowNumber,
                        target = "",
                        status = FavoritesImportEntryStatus.SKIPPED,
                        reason = FavoritesImportEntryReason.INVALID_ITEM,
                    )
                    return@forEachIndexed
                }

                val dedupKey = "${parsed.service}:${parsed.id}"
                if (!seen.add(dedupKey)) {
                    resultEntries += FavoritesImportEntry(
                        rowNumber = rowNumber,
                        target = dedupKey,
                        status = FavoritesImportEntryStatus.SKIPPED,
                        reason = FavoritesImportEntryReason.DUPLICATE_IN_FILE,
                    )
                    return@forEachIndexed
                }

                val imported = importExportRepository.addFavoriteArtist(
                    service = parsed.service,
                    id = parsed.id,
                )
                resultEntries += FavoritesImportEntry(
                    rowNumber = rowNumber,
                    target = dedupKey,
                    status = if (imported) FavoritesImportEntryStatus.SUCCESS else FavoritesImportEntryStatus.FAILED,
                    reason = if (imported) FavoritesImportEntryReason.NONE else FavoritesImportEntryReason.REQUEST_FAILED,
                )
            }

            runCatching { favoritesRepository.refreshFavoriteArtists(site = site) }
        }

        return FavoritesImportResult(entries = resultEntries)
    }

    private suspend fun importPosts(
        site: SelectedSite,
        rawJson: String,
    ): FavoritesImportResult {
        val root = parseArrayOrThrow(rawJson)
        val seen = HashSet<String>(root.size())
        val resultEntries = ArrayList<FavoritesImportEntry>(root.size())

        selectedSiteUseCase.withSite(site) {
            root.forEachIndexed { index, element ->
                val rowNumber = index + 1
                val parsed = parsePostItem(element)
                if (parsed == null) {
                    resultEntries += FavoritesImportEntry(
                        rowNumber = rowNumber,
                        target = "",
                        status = FavoritesImportEntryStatus.SKIPPED,
                        reason = FavoritesImportEntryReason.INVALID_ITEM,
                    )
                    return@forEachIndexed
                }

                val dedupKey = "${parsed.service}:${parsed.creatorId}:${parsed.postId}"
                if (!seen.add(dedupKey)) {
                    resultEntries += FavoritesImportEntry(
                        rowNumber = rowNumber,
                        target = dedupKey,
                        status = FavoritesImportEntryStatus.SKIPPED,
                        reason = FavoritesImportEntryReason.DUPLICATE_IN_FILE,
                    )
                    return@forEachIndexed
                }

                val imported = importExportRepository.addFavoritePost(
                    service = parsed.service,
                    creatorId = parsed.creatorId,
                    postId = parsed.postId,
                )
                resultEntries += FavoritesImportEntry(
                    rowNumber = rowNumber,
                    target = dedupKey,
                    status = if (imported) FavoritesImportEntryStatus.SUCCESS else FavoritesImportEntryStatus.FAILED,
                    reason = if (imported) FavoritesImportEntryReason.NONE else FavoritesImportEntryReason.REQUEST_FAILED,
                )
            }

            runCatching { favoritesRepository.getFavoritePosts(site = site, refresh = true) }
        }

        return FavoritesImportResult(entries = resultEntries)
    }

    private fun parseArtistItem(element: JsonElement): ArtistImportItem? {
        val obj = element.asObjectOrNull() ?: return null
        val service = obj.stringField("service") ?: return null
        val id = obj.stringField("id") ?: return null
        return ArtistImportItem(
            service = service,
            id = id,
        )
    }

    private fun parsePostItem(element: JsonElement): PostImportItem? {
        val obj = element.asObjectOrNull() ?: return null
        val service = obj.stringField("service") ?: return null
        val creatorId = obj.stringField("user") ?: return null
        val postId = obj.stringField("id") ?: return null
        return PostImportItem(
            service = service,
            creatorId = creatorId,
            postId = postId,
        )
    }

    private fun parseArrayOrThrow(rawJson: String) = runCatching {
        val parsed = JsonParser.parseString(rawJson)
        if (!parsed.isJsonArray) error("Import file is not a JSON array")
        parsed.asJsonArray
    }.getOrElse { error("Invalid import file format") }

    private fun JsonElement.asObjectOrNull(): JsonObject? =
        if (isJsonObject) asJsonObject else null

    private fun JsonObject.stringField(name: String): String? =
        runCatching { get(name) }
            .getOrNull()
            ?.takeIf { !it.isJsonNull }
            ?.let { runCatching { it.asString }.getOrNull() }
            ?.trim()
            ?.takeIf { it.isNotEmpty() }

    private data class ArtistImportItem(
        val service: String,
        val id: String,
    )

    private data class PostImportItem(
        val service: String,
        val creatorId: String,
        val postId: String,
    )
}
