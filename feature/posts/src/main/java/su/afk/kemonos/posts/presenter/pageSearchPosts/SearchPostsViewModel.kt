package su.afk.kemonos.posts.presenter.pageSearchPosts

import androidx.paging.cachedIn
import androidx.paging.filter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.posts.domain.pagingSearch.GetSearchPostsPagingUseCase
import su.afk.kemonos.posts.presenter.common.POSTS_SEARCH_DEBOUNCE_MILLIS
import su.afk.kemonos.posts.presenter.common.observeBlacklistedAuthorKeys
import su.afk.kemonos.posts.presenter.common.observeDistinct
import su.afk.kemonos.posts.presenter.delegates.NavigateToPostDelegate
import su.afk.kemonos.posts.presenter.pageSearchPosts.SearchPostsState.*
import su.afk.kemonos.posts.presenter.pageSearchPosts.model.SearchLoadRequest
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.storage.api.repository.blacklist.IStoreBlacklistedAuthorsRepository
import su.afk.kemonos.storage.api.repository.blacklist.blacklistKey
import su.afk.kemonos.storage.api.repository.postsSearchHistory.IStoragePostsSearchHistoryRepository
import su.afk.kemonos.ui.components.posts.filter.PostMediaFilter
import su.afk.kemonos.ui.components.posts.filter.matchesMediaFilter
import su.afk.kemonos.ui.presenter.changeSite.SiteAwareBaseViewModelNew
import javax.inject.Inject

@HiltViewModel
internal class SearchPostsViewModel @Inject constructor(
    private val getSearchPostsPagingUseCase: GetSearchPostsPagingUseCase,
    private val navigateToPostDelegate: NavigateToPostDelegate,
    private val uiSetting: IUiSettingUseCase,
    private val blacklistedAuthorsRepository: IStoreBlacklistedAuthorsRepository,
    private val postsSearchHistoryRepository: IStoragePostsSearchHistoryRepository,
    override val selectedSiteUseCase: ISelectedSiteUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : SiteAwareBaseViewModelNew<State, Event, Effect>() {

    override fun createInitialState(): State = State()

    private val searchQueryFlow = MutableStateFlow("")
    private val mediaFilterFlow = MutableStateFlow(PostMediaFilter())
    private val loadSiteFlow = MutableStateFlow<SelectedSite?>(null)
    private val manualRefreshCounterFlow = MutableStateFlow(0L)

    private val recentSearchLimit = 25
    private var siteInitializedFromSettings = false
    private var lastDefaultSite: SelectedSite? = null

    init {
        observeUiSetting()
        observeSearchPipeline()
        observeRecentSearches()
        initSiteAware()
    }

    override fun onRetry() {
        triggerManualRefresh()
    }

    override suspend fun reloadSite(site: SelectedSite) {
        // Disable loading while resetting query/filter context to avoid transient load on old site.
        loadSiteFlow.value = null

        setState { copy(searchQuery = "") }
        searchQueryFlow.value = ""

        loadSiteFlow.value = site
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.SearchQueryChanged -> onSearchQueryChanged(event.value)
            Event.SearchSubmitted -> onSearchSubmitted()
            Event.PullRefresh -> onPullRefresh()
            is Event.RecentSearchSelected -> onRecentSearchSelected(event.value)
            is Event.RemoveRecentSearch -> onRemoveRecentSearch(event.value)
            Event.ToggleHasVideo -> toggleHasVideo()
            Event.ToggleHasAttachments -> toggleHasAttachments()
            Event.ToggleHasImages -> toggleHasImages()
            is Event.NavigateToPost -> navigateToPost(event.post)
            Event.RandomPost -> randomPost()
            Event.SwitchSite -> switchSite()
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchPipeline() {
        var lastManualRefreshCounter = 0L

        val debouncedQueryFlow = searchQueryFlow
            .debounce { query -> if (query.isBlank()) 0L else POSTS_SEARCH_DEBOUNCE_MILLIS }
            .map { it.trim() }
            .distinctUntilChanged()

        val blacklistedKeysFlow = blacklistedAuthorsRepository.observeBlacklistedAuthorKeys()

        combine(
            loadSiteFlow.filterNotNull(),
            debouncedQueryFlow,
            mediaFilterFlow,
            blacklistedKeysFlow,
            manualRefreshCounterFlow,
        ) { site, query, mediaFilter, blacklistedAuthorKeys, manualRefreshCounter ->
            SearchLoadRequest(
                site = site,
                search = query.ifEmpty { null },
                mediaFilter = mediaFilter,
                blacklistedAuthorKeys = blacklistedAuthorKeys,
                manualRefreshCounter = manualRefreshCounter,
            )
        }
            .onEach { request ->
                val forceRefresh = request.manualRefreshCounter != lastManualRefreshCounter
                lastManualRefreshCounter = request.manualRefreshCounter

                postsSearchHistoryRepository.save(
                    site = request.site,
                    query = request.search.orEmpty(),
                    limit = recentSearchLimit,
                )

                val pagingFlow = getSearchPostsPagingUseCase(
                    site = request.site,
                    tag = null,
                    search = request.search,
                    forceRefresh = forceRefresh,
                )

                setState {
                    copy(
                        posts = if (request.mediaFilter.isActive || request.blacklistedAuthorKeys.isNotEmpty()) {
                            pagingFlow
                                .map { page ->
                                    page.filter { post ->
                                        val allowedByBlacklist = !request.blacklistedAuthorKeys.contains(
                                            blacklistKey(post.service, post.userId),
                                        )
                                        allowedByBlacklist && post.matchesMediaFilter(request.mediaFilter)
                                    }
                                }
                                .cachedIn(viewModelScope)
                        } else {
                            pagingFlow.cachedIn(viewModelScope)
                        },
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeRecentSearches() {
        loadSiteFlow
            .filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { currentSite ->
                postsSearchHistoryRepository.observeRecent(currentSite, recentSearchLimit)
            }
            .onEach { items ->
                setState { copy(recentSearches = items) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeUiSetting() {
        uiSetting.observeDistinct(viewModelScope) { model ->
            if (!siteInitializedFromSettings) {
                siteInitializedFromSettings = true
                lastDefaultSite = model.defaultSite
                viewModelScope.launch {
                    selectedSiteUseCase.setSite(model.defaultSite)
                }
            } else if (model.defaultSite != lastDefaultSite) {
                lastDefaultSite = model.defaultSite
                viewModelScope.launch {
                    selectedSiteUseCase.setSite(model.defaultSite)
                }
            }
            setState { copy(uiSettingModel = model) }
        }
    }

    private fun onPullRefresh() {
        triggerManualRefresh()
    }

    private fun triggerManualRefresh() {
        manualRefreshCounterFlow.update { it + 1 }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        setState { copy(searchQuery = newQuery) }
        searchQueryFlow.value = newQuery
    }

    private fun onSearchSubmitted() = viewModelScope.launch {
        postsSearchHistoryRepository.save(
            site = site.value,
            query = currentState.searchQuery,
            limit = recentSearchLimit,
        )
    }

    private fun onRecentSearchSelected(query: String) {
        setState { copy(searchQuery = query) }
        searchQueryFlow.value = query
        onSearchSubmitted()
    }

    private fun onRemoveRecentSearch(query: String) = viewModelScope.launch {
        postsSearchHistoryRepository.delete(
            site = site.value,
            query = query,
        )
    }

    private fun toggleHasVideo() {
        val next = currentState.mediaFilter.copy(hasVideo = !currentState.mediaFilter.hasVideo)
        mediaFilterFlow.value = next
        setState { copy(mediaFilter = next) }
    }

    private fun toggleHasAttachments() {
        val next = currentState.mediaFilter.copy(hasAttachments = !currentState.mediaFilter.hasAttachments)
        mediaFilterFlow.value = next
        setState { copy(mediaFilter = next) }
    }

    private fun toggleHasImages() {
        val next = currentState.mediaFilter.copy(hasImages = !currentState.mediaFilter.hasImages)
        mediaFilterFlow.value = next
        setState { copy(mediaFilter = next) }
    }

    private fun randomPost() = viewModelScope.launch {
        navigateToPostDelegate.navigateToRandomPost()
    }

    private fun navigateToPost(post: PostDomain) {
        viewModelScope.launch {
            navigateToPostDelegate.navigateToPost(post)
        }
    }
}
