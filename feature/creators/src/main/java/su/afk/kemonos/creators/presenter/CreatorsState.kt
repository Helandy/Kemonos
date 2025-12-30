package su.afk.kemonos.creators.presenter

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.domain.models.Creators
import su.afk.kemonos.domain.domain.models.CreatorsSort

data class CreatorsState(
    val loading: Boolean = false,
    /** Сетевое обновление кэша (ensureFresh). */
    val refreshing: Boolean = false,

    /** Список сервисов для фильтра. */
    val services: List<String> = listOf("All"),

    val selectedService: String = "All",
    val searchQuery: String = "",
    val sortedType: CreatorsSort = CreatorsSort.POPULARITY,
    val sortAscending: Boolean = false,

    /** Пейджинг-результаты из БД. */
    val creatorsPaged: Flow<PagingData<Creators>> = flowOf(PagingData.empty()),

    val selectedSite: SelectedSite = SelectedSite.K,
)