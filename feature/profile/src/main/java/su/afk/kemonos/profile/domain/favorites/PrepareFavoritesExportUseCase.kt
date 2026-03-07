package su.afk.kemonos.profile.domain.favorites

import com.google.gson.JsonParser
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.site.withSite
import su.afk.kemonos.profile.domain.repository.IImportExportRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

internal enum class FavoritesExportType {
    ARTISTS,
    POSTS,
}

internal data class FavoritesExportPayload(
    val fileName: String,
    val json: String,
)

internal class PrepareFavoritesExportUseCase @Inject constructor(
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    private val importExportRepository: IImportExportRepository,
) {
    suspend operator fun invoke(
        site: SelectedSite,
        type: FavoritesExportType,
    ): FavoritesExportPayload {
        val rawJson = selectedSiteUseCase.withSite(site) {
            when (type) {
                FavoritesExportType.ARTISTS -> importExportRepository.getFavoriteArtistsRaw()
                FavoritesExportType.POSTS -> importExportRepository.getFavoritePostsRaw()
            }
        }

        val count = extractCount(rawJson)
        val datePart = LocalDate.now().format(exportDateFormatter)
        val sitePart = if (site == SelectedSite.K) "Kemono" else "Coomer"
        val typePart = when (type) {
            FavoritesExportType.ARTISTS -> "Artist"
            FavoritesExportType.POSTS -> "Post"
        }

        return FavoritesExportPayload(
            fileName = "${sitePart}_${typePart}_(${count})_${datePart}.json",
            json = rawJson,
        )
    }

    private fun extractCount(rawJson: String): Int {
        return runCatching {
            val parsed = JsonParser.parseString(rawJson)
            if (parsed.isJsonArray) parsed.asJsonArray.size() else 0
        }.getOrDefault(0)
    }

    private companion object {
        val exportDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd_MM_yy")
    }
}
