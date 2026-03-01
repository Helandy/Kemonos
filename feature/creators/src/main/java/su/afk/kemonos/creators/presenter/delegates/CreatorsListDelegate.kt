package su.afk.kemonos.creators.presenter.delegates

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import su.afk.kemonos.creators.domain.GetCreatorsPagedUseCase
import su.afk.kemonos.domain.models.creator.Creators.Companion.toFavoriteArtistUi
import su.afk.kemonos.domain.models.creator.CreatorsSort
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import javax.inject.Inject

internal class CreatorsListDelegate @Inject constructor(
    private val getCreatorsPagedUseCase: GetCreatorsPagedUseCase,
) {
    /** Получение авторов с фильтрами */
    fun creatorsPagedFlow(
        service: String?,
        searchQuery: String,
        sortedType: CreatorsSort,
        sortAscending: Boolean,
    ): Flow<PagingData<FavoriteArtist>> =
        getCreatorsPagedUseCase.paging(
            service = service,
            query = searchQuery,
            sort = sortedType,
            ascending = sortAscending
        )
            .map { paging ->
                paging.map { it.toFavoriteArtistUi() }
            }
}
