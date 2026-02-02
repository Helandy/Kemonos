package su.afk.kemonos.creators.presenter

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import su.afk.kemonos.common.presenter.baseViewModel.UiEffect
import su.afk.kemonos.common.presenter.baseViewModel.UiEvent
import su.afk.kemonos.common.presenter.baseViewModel.UiState
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.CreatorsSort
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.preferences.ui.UiSettingModel

internal class CreatorsState {
    data class State(
        val loading: Boolean = false,
        /** Сетевое обновление кэша (ensureFresh). */
        val refreshing: Boolean = false,

        /** Список сервисов для фильтра. */
        val services: List<String> = listOf("Services"),

        val selectedService: String = "Services",
        val searchQuery: String = "",
        val sortedType: CreatorsSort = CreatorsSort.POPULARITY,
        val sortAscending: Boolean = false,

        /** Пейджинг-результаты из БД. */
        val creatorsPaged: Flow<PagingData<FavoriteArtist>> = flowOf(PagingData.empty()),

        /** Рандомные авторы */
        val randomSuggestions: List<FavoriteArtist> = emptyList(),
        val randomSuggestionsFiltered: List<FavoriteArtist> = emptyList(),
        val randomSuggestionsLoading: Boolean = false,

        val selectedSite: SelectedSite = SelectedSite.K,
        val uiSettingModel: UiSettingModel = UiSettingModel(),
    ) : UiState

    sealed interface Event : UiEvent {
        data class QueryChanged(val value: String) : Event
        data class ServiceSelected(val value: String) : Event
        data class SortSelected(val value: CreatorsSort) : Event
        data object ToggleSortOrder : Event

        data class CreatorClicked(val creator: FavoriteArtist) : Event
        data object RandomClicked : Event
        data object SwitchSiteClicked : Event
    }

    sealed interface Effect : UiEffect {
        data object ScrollToTop : Effect
    }
}