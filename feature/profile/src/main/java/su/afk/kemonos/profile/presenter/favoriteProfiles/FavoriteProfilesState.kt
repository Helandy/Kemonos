package su.afk.kemonos.profile.presenter.favoriteProfiles

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.common.presenter.baseViewModel.UiEffect
import su.afk.kemonos.common.presenter.baseViewModel.UiEvent
import su.afk.kemonos.common.presenter.baseViewModel.UiState
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.profile.api.domain.favoriteProfiles.FavoriteSortedType
import su.afk.kemonos.profile.api.domain.favoriteProfiles.FreshFavoriteArtistKey

internal class FavoriteProfilesState {
    data class State(
        val loading: Boolean = false,
        /** Сетевое обновление кэша (ensureFresh / refresh). */
        val refreshing: Boolean = false,

        val services: List<String> = listOf("Services"),
        val selectedService: String = "Services",

        val searchQuery: String = "",

        val sortedType: FavoriteSortedType = FavoriteSortedType.NewPostsDate,
        val sortAscending: Boolean = false,

        /** Пейджинг-результаты из БД. */
        val artistsPaged: Flow<PagingData<FavoriteArtist>> = emptyFlow(),
        val freshSet: Set<FreshFavoriteArtistKey> = emptySet(),

        val selectedSite: SelectedSite = SelectedSite.K,
        val uiSettingModel: UiSettingModel = UiSettingModel(),
    ) : UiState

    sealed interface Event : UiEvent {
        data class QueryChanged(val value: String) : Event
        data class ServiceSelected(val value: String) : Event
        data class SortSelected(val value: FavoriteSortedType) : Event
        data object ToggleSortOrder : Event

        data object Refresh : Event
        data object Retry : Event

        data class CreatorClicked(val creator: FavoriteArtist, val isFresh: Boolean) : Event
    }

    sealed interface Effect : UiEffect {
        data object ScrollToTop : Effect
    }
}
