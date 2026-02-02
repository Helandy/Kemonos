package su.afk.kemonos.creators.presenter.delegates

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import su.afk.kemonos.creators.domain.GetCreatorsPagedUseCase
import su.afk.kemonos.creators.presenter.CreatorsState.Effect
import su.afk.kemonos.creators.presenter.CreatorsState.State
import su.afk.kemonos.domain.models.creator.Creators.Companion.toFavoriteArtistUi
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import javax.inject.Inject

internal class CreatorsListDelegate @Inject constructor(
    private val getCreatorsPagedUseCase: GetCreatorsPagedUseCase,
    private val uiSetting: IUiSettingUseCase,
) {
    private var searchDebounceJob: Job? = null

    fun observeUiSetting(scope: CoroutineScope, setState: (State.() -> State) -> Unit) {
        uiSetting.prefs
            .distinctUntilChanged()
            .onEach { model -> setState { copy(uiSettingModel = model) } }
            .launchIn(scope)
    }

    fun rebuildPaging(
        scope: CoroutineScope,
        state: () -> State,
        setState: (State.() -> State) -> Unit,
        setEffect: (Effect) -> Unit,
        scrollToTop: Boolean,
    ) {
        val s = state().selectedService
        val qRaw = state().searchQuery
        val sort = state().sortedType
        val asc = state().sortAscending

        val flow: Flow<PagingData<FavoriteArtist>> =
            getCreatorsPagedUseCase
                .paging(service = s, query = qRaw, sort = sort, ascending = asc)
                .map { paging -> paging.map { it.toFavoriteArtistUi() } }
                .cachedIn(scope)

        setState { copy(creatorsPaged = flow) }
        if (scrollToTop) setEffect(Effect.ScrollToTop)
    }

    /** Грузим random ТОЛЬКО когда реально надо (обычно на старте/смене сайта). */
    fun loadRandom(
        scope: CoroutineScope,
        state: () -> State,
        setState: (State.() -> State) -> Unit,
        setEffect: (Effect) -> Unit,
    ) {
        val suggestEnabled = state().uiSettingModel.suggestRandomAuthors
        if (!suggestEnabled) {
            setState {
                copy(
                    randomSuggestions = emptyList(),
                    randomSuggestionsFiltered = emptyList(),
                    randomSuggestionsLoading = false,
                )
            }
            return
        }

        val service = state().selectedService

        scope.launch {
            setState { copy(randomSuggestionsLoading = true) }
            runCatching {
                getCreatorsPagedUseCase.randomSuggestions(service = service, query = "", limit = 50)
            }.onSuccess { list ->
                val mapped = list.map { it.toFavoriteArtistUi() }
                setState {
                    copy(
                        randomSuggestions = mapped,
                        randomSuggestionsLoading = false,
                    )
                }
                applyRandomFilter(state().searchQuery, state, setState)
            }.onFailure {
                setState { copy(randomSuggestionsLoading = false) }
            }
        }
    }

    /** Только локальная фильтрация (порядок НЕ меняется). */
    fun applyRandomFilter(
        query: String,
        state: () -> State,
        setState: (State.() -> State) -> Unit,
    ) {
        val list = state().randomSuggestions
        val q = query.trim()

        val filtered = if (q.isEmpty()) list else {
            val needle = q.lowercase()
            list.filter { a ->
                a.name.orEmpty().lowercase().contains(needle) ||
                        a.service.lowercase().contains(needle)
            }
        }
        setState { copy(randomSuggestionsFiltered = filtered) }
    }

    fun updateSearch(
        scope: CoroutineScope,
        state: () -> State,
        setState: (State.() -> State) -> Unit,
        setEffect: (Effect) -> Unit,
        query: String,
        debounceMs: Long = 350L,
    ) {
        setState { copy(searchQuery = query) }

        // ✅ всегда фильтруем random локально
        applyRandomFilter(query, state, setState)

        searchDebounceJob?.cancel()

        val trimmed = query.trim()

        // ✅ если пусто — просто перестроим paging (без random load!)
        if (trimmed.isEmpty()) {
            rebuildPaging(scope, state, setState, setEffect, scrollToTop = true)
            return
        }

        if (trimmed.length < 2) return

        searchDebounceJob = scope.launch {
            delay(debounceMs)
            val latest = state().searchQuery.trim()
            if (latest != trimmed) return@launch
            rebuildPaging(scope, state, setState, setEffect, scrollToTop = true)
        }
    }
}
