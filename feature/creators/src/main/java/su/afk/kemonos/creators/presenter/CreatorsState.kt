package su.afk.kemonos.creators.presenter

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.Creators
import su.afk.kemonos.domain.models.CreatorsSort
import su.afk.kemonos.preferences.ui.UiSettingModel

data class CreatorsState(
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
    val creatorsPaged: Flow<PagingData<Creators>> = flowOf(PagingData.empty()),

    /** Рандомные авторы */
    val randomSuggestions: List<Creators> = emptyList(),

    val selectedSite: SelectedSite = SelectedSite.K,
    val uiSettingModel: UiSettingModel = UiSettingModel(),
)