package su.afk.kemonos.profile.domain.favorites.model

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.domain.favorites.FavoritesImportType

internal data class FavoritesImportRequest(
    val site: SelectedSite,
    val type: FavoritesImportType,
)