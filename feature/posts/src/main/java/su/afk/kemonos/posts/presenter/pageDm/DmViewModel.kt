package su.afk.kemonos.posts.presenter.pageDm

import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.posts.domain.pagingDms.GetDmsPagingUseCase
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.ui.presenter.changeSite.SiteAwareBaseViewModelNew
import javax.inject.Inject

@HiltViewModel
internal class DmViewModel @Inject constructor(
    private val getDmsPagingUseCase: GetDmsPagingUseCase,
    private val uiSetting: IUiSettingUseCase,
    private val navManager: NavigationManager,
    private val creatorProfileNavigator: ICreatorProfileNavigator,
    override val selectedSiteUseCase: ISelectedSiteUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : SiteAwareBaseViewModelNew<DmState.State, DmState.Event, DmState.Effect>() {

    override fun createInitialState(): DmState.State = DmState.State()
    private val searchQueryFlow = MutableStateFlow("")

    init {
        observeUiSetting()
        observeSearchQuery()
        initSiteAware()
    }

    override fun onRetry() {
        loadDms(site.value, currentState.searchQuery)
    }

    override suspend fun reloadSite(site: SelectedSite) {
        loadDms(site, currentState.searchQuery)
    }

    override fun onEvent(event: DmState.Event) {
        when (event) {
            is DmState.Event.SearchQueryChanged -> onSearchQueryChanged(event.value)
            DmState.Event.SearchSubmitted -> onSearchSubmitted()
            is DmState.Event.NavigateToProfile -> navigateToProfile(event.service, event.id)
            DmState.Event.SwitchSite -> switchSite()
        }
    }

    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged()
            .onEach { model ->
                setState { copy(uiSettingModel = model) }
            }
            .launchIn(viewModelScope)
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() = viewModelScope.launch {
        searchQueryFlow
            .debounce(700L)
            .distinctUntilChanged()
            .collectLatest { query ->
                loadDms(site.value, query)
            }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        setState { copy(searchQuery = newQuery) }
        searchQueryFlow.value = newQuery
    }

    private fun onSearchSubmitted() {
        loadDms(site.value, currentState.searchQuery)
    }

    private fun loadDms(site: SelectedSite, query: String?) {
        val normalizedQuery = query?.trim()?.ifEmpty { null }
        setState {
            copy(
                dms = getDmsPagingUseCase(
                    site = site,
                    query = normalizedQuery,
                ).cachedIn(viewModelScope)
            )
        }
    }

    private fun navigateToProfile(service: String, id: String) = viewModelScope.launch {
        navManager.navigate(
            creatorProfileNavigator.getCreatorProfileDest(
                service = service,
                id = id,
                isFresh = false,
            )
        )
    }
}
