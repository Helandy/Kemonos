package su.afk.kemonos.profile.presenter.favoriteProfiles

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.api.model.FavoriteArtist
import su.afk.kemonos.profile.presenter.favoriteProfiles.views.FavoriteSortedType

internal data class FavoriteProfilesState(
    val selectSite: SelectedSite = SelectedSite.K,

    val loading: Boolean = true,

    val favoriteProfiles: List<FavoriteArtist> = emptyList(),

    val searchCreators: List<FavoriteArtist> = emptyList(),
    val searchQuery: String = "",

    val selectedService: String = "Services",
    val sortedType: FavoriteSortedType = FavoriteSortedType.NewPostsDate,
    val sortAscending: Boolean = false,
)
