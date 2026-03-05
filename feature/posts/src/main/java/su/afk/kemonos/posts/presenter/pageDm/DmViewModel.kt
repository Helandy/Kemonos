package su.afk.kemonos.posts.presenter.pageDm

import androidx.paging.cachedIn
import androidx.paging.filter
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
import su.afk.kemonos.posts.presenter.common.POSTS_SEARCH_DEBOUNCE_MILLIS
import su.afk.kemonos.posts.presenter.common.observeBlacklistedAuthorKeys
import su.afk.kemonos.posts.presenter.common.observeDistinct
import su.afk.kemonos.posts.presenter.pageDm.model.DmLoadRequest
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.storage.api.repository.blacklist.IStoreBlacklistedAuthorsRepository
import su.afk.kemonos.storage.api.repository.blacklist.blacklistKey
import su.afk.kemonos.ui.presenter.changeSite.SiteAwareBaseViewModelNew
import javax.inject.Inject

@HiltViewModel
internal class DmViewModel @Inject constructor(
    private val getDmsPagingUseCase: GetDmsPagingUseCase,
    private val uiSetting: IUiSettingUseCase,
    private val blacklistedAuthorsRepository: IStoreBlacklistedAuthorsRepository,
    private val navManager: NavigationManager,
    private val creatorProfileNavigator: ICreatorProfileNavigator,
    override val selectedSiteUseCase: ISelectedSiteUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : SiteAwareBaseViewModelNew<DmState.State, DmState.Event, DmState.Effect>() {

    override fun createInitialState(): DmState.State = DmState.State()

    private val searchQueryFlow = MutableStateFlow("")
    private val loadSiteFlow = MutableStateFlow<SelectedSite?>(null)
    private val manualRefreshCounterFlow = MutableStateFlow(0L)

    init {
        observeUiSetting()
        observeDmsPipeline()
        initSiteAware()
    }

    override fun onRetry() {
        triggerManualRefresh()
    }

    override suspend fun reloadSite(site: SelectedSite) {
        loadSiteFlow.value = null

        setState { copy(searchQuery = "") }
        searchQueryFlow.value = ""

        loadSiteFlow.value = site
    }

    override fun onEvent(event: DmState.Event) {
        when (event) {
            is DmState.Event.SearchQueryChanged -> onSearchQueryChanged(event.value)
            DmState.Event.SearchSubmitted -> onSearchSubmitted()
            DmState.Event.PullRefresh -> onPullRefresh()
            is DmState.Event.NavigateToProfile -> navigateToProfile(event.service, event.id)
            DmState.Event.SwitchSite -> switchSite()
        }
    }

    private fun observeUiSetting() {
        uiSetting.observeDistinct(viewModelScope) { model ->
            setState { copy(uiSettingModel = model) }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeDmsPipeline() {
        var lastManualRefreshCounter = 0L
        val blacklistedKeysFlow = blacklistedAuthorsRepository.observeBlacklistedAuthorKeys()

        val loadRequestFlow = combine(
            loadSiteFlow.filterNotNull(),
            searchQueryFlow
                .debounce { query -> if (query.isBlank()) 0L else POSTS_SEARCH_DEBOUNCE_MILLIS }
                .map { it.trim() }
                .distinctUntilChanged(),
            manualRefreshCounterFlow,
        ) { site, query, manualRefreshCounter ->
            DmLoadRequest(
                site = site,
                query = query,
                manualRefreshCounter = manualRefreshCounter,
            )
        }
        val basePagingFlow = loadRequestFlow
            .map { request ->
                val forceRefresh = request.manualRefreshCounter != lastManualRefreshCounter
                lastManualRefreshCounter = request.manualRefreshCounter

                getDmsPagingUseCase(
                    site = request.site,
                    query = request.query.ifEmpty { null },
                    forceRefresh = forceRefresh,
                ).cachedIn(viewModelScope)
            }

        combine(basePagingFlow, blacklistedKeysFlow) { pagingFlow, blacklistedKeys ->
            pagingFlow to blacklistedKeys
        }
            .onEach { (pagingFlow, blacklistedKeys) ->
                setState {
                    copy(
                        dms = if (blacklistedKeys.isEmpty()) {
                            pagingFlow
                        } else {
                            pagingFlow.map { page ->
                                page.filter { dm ->
                                    !blacklistedKeys.contains(blacklistKey(dm.service, dm.artistId))
                                }
                            }
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun onSearchQueryChanged(newQuery: String) {
        setState { copy(searchQuery = newQuery) }
        searchQueryFlow.value = newQuery
    }

    private fun onSearchSubmitted() {
        triggerManualRefresh()
    }

    private fun onPullRefresh() {
        triggerManualRefresh()
    }

    private fun triggerManualRefresh() {
        manualRefreshCounterFlow.update { it + 1 }
    }

    private fun navigateToProfile(service: String, id: String) = viewModelScope.launch {
        navManager.navigate(
            creatorProfileNavigator.getCreatorProfileDest(
                service = service,
                id = id,
            )
        )
    }
}
