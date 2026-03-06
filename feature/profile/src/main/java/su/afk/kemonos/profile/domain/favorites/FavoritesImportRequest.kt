package su.afk.kemonos.profile.domain.favorites

import su.afk.kemonos.domain.SelectedSite

internal data class FavoritesImportRequest(
    val site: SelectedSite,
    val type: FavoritesImportType,
)

