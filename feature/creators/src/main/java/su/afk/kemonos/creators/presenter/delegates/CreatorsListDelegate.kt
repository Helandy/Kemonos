package su.afk.kemonos.creators.presenter.delegates

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import su.afk.kemonos.creators.domain.GetCreatorsPagedUseCase
import su.afk.kemonos.creators.presenter.CreatorsState.Effect
import su.afk.kemonos.creators.presenter.CreatorsState.State
import su.afk.kemonos.domain.models.creator.Creators.Companion.toFavoriteArtistUi
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import javax.inject.Inject

internal class CreatorsListDelegate @Inject constructor(
    private val getCreatorsPagedUseCase: GetCreatorsPagedUseCase,
) {
    private var searchDebounceJob: Job? = null

    /** Получение авторов с фильтрами */
    fun creatorsFilterPaging(
        scope: CoroutineScope,
        state: () -> State,
        setState: (State.() -> State) -> Unit,
        setEffect: (Effect) -> Unit,
        scrollToTop: Boolean,
    ) {
        val service = state().selectedService
        val searchQuery = state().searchQuery
        val sortedType = state().sortedType
        val sortAscending = state().sortAscending

        val flow: Flow<PagingData<FavoriteArtist>> = getCreatorsPagedUseCase.paging(
            service = service,
            query = searchQuery,
            sort = sortedType,
            ascending = sortAscending
        )
            .map { paging ->
                paging.map { it.toFavoriteArtistUi() }
            }
            .cachedIn(scope)

        setState {
            copy(
                creatorsPaged = flow,
            )
        }

        if (scrollToTop) setEffect(Effect.ScrollToTop)
    }

    fun searchQuery(
        scope: CoroutineScope,
        state: () -> State,
        setState: (State.() -> State) -> Unit,
        setEffect: (Effect) -> Unit,
        query: String,
        debounceMs: Long = 350L,
    ) {
        setState { copy(searchQuery = query) }

        searchDebounceJob?.cancel()

        val queryTrimmed = query.trim()
        if (queryTrimmed.isEmpty()) {
            creatorsFilterPaging(scope, state, setState, setEffect, scrollToTop = true)
            return
        }

        if (queryTrimmed.length < 2) return
        searchDebounceJob = scope.launch {
            delay(debounceMs)
            creatorsFilterPaging(scope, state, setState, setEffect, scrollToTop = true)
        }
    }
}
