package su.afk.kemonos.creators.presenter

import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.changeSite.SiteAwareBaseViewModel
import su.afk.kemonos.core.api.domain.useCase.ISelectedSiteUseCase
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.creators.domain.GetCreatorsPagedUseCase
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.domain.models.Creators
import su.afk.kemonos.domain.domain.models.CreatorsSort
import su.afk.kemonos.navigation.NavigationManager
import javax.inject.Inject

@HiltViewModel
internal class CreatorsViewModel @Inject constructor(
    private val getCreatorsPagedUseCase: GetCreatorsPagedUseCase,
    private val navManager: NavigationManager,
    private val creatorProfileNavigator: ICreatorProfileNavigator,
    override val selectedSiteUseCase: ISelectedSiteUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : SiteAwareBaseViewModel<CreatorsState>(
    initialState = CreatorsState(),
) {

    override fun onRetry() {
        viewModelScope.launch {
            ensureFreshAndReloadServices()
            rebuildPaging()
        }
    }

    init {
        initSiteAware()
    }

    override suspend fun reloadSite(site: SelectedSite) {
        setState {
            copy(
                selectedService = "All",
                searchQuery = "",
                sortedType = CreatorsSort.POPULARITY,
                sortAscending = false,
            )
        }

        rebuildPaging()
        ensureFreshAndReloadServices()
    }

    fun setService(service: String) {
        setState { copy(selectedService = service) }
        rebuildPaging()
    }

    fun setSortType(type: CreatorsSort) {
        setState { copy(sortedType = type) }
        rebuildPaging()
    }

    fun toggleSortOrder() {
        setState { copy(sortAscending = !state.value.sortAscending) }
        rebuildPaging()
    }

    private fun rebuildPaging() {
        val s = state.value.selectedService
        val q = state.value.searchQuery
        val sort = state.value.sortedType
        val asc = state.value.sortAscending

        val flow = getCreatorsPagedUseCase
            .paging(service = s, query = q, sort = sort, ascending = asc)
            .cachedIn(viewModelScope)

        setState { copy(creatorsPaged = flow) }
    }

    private suspend fun ensureFreshAndReloadServices() {
        loadServicesOnce()
        val updated = ensureFresh()
        if (updated) {
            loadServicesOnce()
            rebuildPaging()
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

    fun onCreatorClick(creator: Creators) {
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
            rebuildPaging()
            return
        }
        if (trimmed.length < 2) return

        searchDebounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            val latest = state.value.searchQuery.trim()
            if (latest != trimmed) return@launch
            rebuildPaging()
        }
    }

    private var searchDebounceJob: Job? = null

    private companion object {
        private const val SEARCH_DEBOUNCE_MS = 350L
    }
}