package su.afk.kemonos.creators.presenter

import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.changeSite.SiteAwareBaseViewModelNew
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.creators.domain.GetCreatorsPagedUseCase
import su.afk.kemonos.creators.domain.RandomCreatorUseCase
import su.afk.kemonos.creators.presenter.CreatorsState.*
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.Creators
import su.afk.kemonos.domain.models.CreatorsSort
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import javax.inject.Inject

@HiltViewModel
internal class CreatorsViewModel @Inject constructor(
    private val getCreatorsPagedUseCase: GetCreatorsPagedUseCase,
    private val navManager: NavigationManager,
    private val creatorProfileNavigator: ICreatorProfileNavigator,
    private val randomCreatorUseCase: RandomCreatorUseCase,
    private val uiSetting: IUiSettingUseCase,
    override val selectedSiteUseCase: ISelectedSiteUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : SiteAwareBaseViewModelNew<State, Event, Effect>() {

    override fun createInitialState(): State = State()

    override fun onRetry() {
        viewModelScope.launch {
            ensureFreshAndReloadServices()
        }
    }

    /** Смена сайта и первичная загрузка */
    override suspend fun reloadSite(site: SelectedSite) {
        setState {
            copy(
                selectedService = "Services",
                searchQuery = "",
                sortedType = CreatorsSort.POPULARITY,
                sortAscending = false,
            )
        }

        rebuildPaging(scrollToTop = true)
        ensureFreshAndReloadServices()
    }

    init {
        observeUiSetting()
        initSiteAware()
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.QueryChanged -> updateSearch(event.value)
            is Event.ServiceSelected -> setService(event.value)
            is Event.SortSelected -> setSortType(event.value)
            Event.ToggleSortOrder -> toggleSortOrder()
            is Event.CreatorClicked -> onCreatorClick(event.creator)
            Event.RandomClicked -> randomCreator()
            Event.SwitchSiteClicked -> switchSite()
        }
    }

    /** UI настройки */
    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged()
            .onEach { model ->
                setState { copy(uiSettingModel = model) }
            }
            .launchIn(viewModelScope)
    }

    fun setService(service: String) {
        setState { copy(selectedService = service) }
        rebuildPaging(scrollToTop = true)
    }

    fun setSortType(type: CreatorsSort) {
        setState { copy(sortedType = type) }
        rebuildPaging(scrollToTop = true)
    }

    fun toggleSortOrder() {
        setState { copy(sortAscending = !state.value.sortAscending) }
        rebuildPaging(scrollToTop = true)
    }

    private fun rebuildPaging(scrollToTop: Boolean) {
        val s = state.value.selectedService
        val qRaw = state.value.searchQuery
        val q = qRaw.trim()
        val sort = state.value.sortedType
        val asc = state.value.sortAscending

        val flow = getCreatorsPagedUseCase
            .paging(service = s, query = qRaw, sort = sort, ascending = asc)
            .cachedIn(viewModelScope)

        setState { copy(creatorsPaged = flow) }

        if (scrollToTop) setEffect(Effect.ScrollToTop)

        /** random suggestions */
        viewModelScope.launch {
            val suggest = state.value.uiSettingModel.suggestRandomAuthors
            val shouldShow = suggest && q.isEmpty()
            if (!shouldShow) {
                setState { copy(randomSuggestions = emptyList()) }
                return@launch
            }
            val list = getCreatorsPagedUseCase.randomSuggestions(service = s, query = "", limit = 50)
            setState { copy(randomSuggestions = list) }
        }
    }

    private suspend fun ensureFreshAndReloadServices() {
        loadServicesOnce()
        val updated = ensureFresh()
        if (updated) {
            loadServicesOnce()
            rebuildPaging(scrollToTop = true)
        }
    }

    private suspend fun loadServicesOnce() {
        val services = getCreatorsPagedUseCase.getServices()
        setState { copy(services = services) }
    }

    private suspend fun ensureFresh(): Boolean {
        return try {
            setState { copy(refreshing = true) }
            getCreatorsPagedUseCase.ensureFresh()
        } finally {
            setState { copy(refreshing = false) }
        }
    }

    fun onCreatorClick(creator: Creators) = viewModelScope.launch {
        navManager.navigate(
            creatorProfileNavigator.getCreatorProfileDest(
                service = creator.service,
                id = creator.id,
            )
        )
    }

    fun updateSearch(query: String) {
        setState { copy(searchQuery = query) }

        searchDebounceJob?.cancel()
        val trimmed = query.trim()

        if (trimmed.isEmpty()) {
            rebuildPaging(scrollToTop = true)
            return
        }
        if (trimmed.length < 2) return

        searchDebounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            val latest = state.value.searchQuery.trim()
            if (latest != trimmed) return@launch
            rebuildPaging(scrollToTop = true)
        }
    }

    /** Открыть случайного автора */
    fun randomCreator() = viewModelScope.launch {
        try {
            setState { copy(loading = true) }
            val creator = randomCreatorUseCase()

            navManager.navigate(
                creatorProfileNavigator.getCreatorProfileDest(
                    service = creator.service,
                    id = creator.artistId,
                )
            )
        } finally {
            setState { copy(loading = false) }
        }
    }

    private var searchDebounceJob: Job? = null

    private companion object {
        private const val SEARCH_DEBOUNCE_MS = 350L
    }
}