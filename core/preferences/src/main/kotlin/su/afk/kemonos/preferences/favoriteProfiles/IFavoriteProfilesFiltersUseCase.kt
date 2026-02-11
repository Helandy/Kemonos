package su.afk.kemonos.preferences.favoriteProfiles

import su.afk.kemonos.domain.SelectedSite

interface IFavoriteProfilesFiltersUseCase {
    suspend fun read(site: SelectedSite): FavoriteProfilesFiltersPrefs
    suspend fun setSelectedService(site: SelectedSite, value: String)
    suspend fun setSortedTypeName(site: SelectedSite, value: String)
    suspend fun setSortAscending(site: SelectedSite, value: Boolean)
}

data class FavoriteProfilesFiltersPrefs(
    val selectedService: String = "Services",
    val sortedTypeName: String = "NewPostsDate",
    val sortAscending: Boolean = false,
)
