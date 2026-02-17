package su.afk.kemonos.posts.presenter.pageSearchPosts

import androidx.paging.cachedIn
import androidx.paging.filter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.posts.domain.pagingSearch.GetSearchPostsPagingUseCase
import su.afk.kemonos.posts.domain.usecase.GetRandomPost
import su.afk.kemonos.posts.presenter.common.NavigateToPostDelegate
import su.afk.kemonos.posts.presenter.pageSearchPosts.SearchPostsState.*
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.ui.components.posts.filter.PostMediaFilter
import su.afk.kemonos.ui.components.posts.filter.matchesMediaFilter
import su.afk.kemonos.ui.presenter.changeSite.SiteAwareBaseViewModelNew
import javax.inject.Inject

@HiltViewModel
internal class SearchPostsViewModel @Inject constructor(
    private val getSearchPostsPagingUseCase: GetSearchPostsPagingUseCase,
    private val navigateToPostDelegate: NavigateToPostDelegate,
    private val getRandomPost: GetRandomPost,
    private val uiSetting: IUiSettingUseCase,
    override val selectedSiteUseCase: ISelectedSiteUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : SiteAwareBaseViewModelNew<State, Event, Effect>() {

    override fun createInitialState(): State = State()

    /** Flow для текстового поиска */
    private val searchQueryFlow = MutableStateFlow("")
    private val mediaFilterFlow = MutableStateFlow(PostMediaFilter())

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() = viewModelScope.launch {
        combine(
            searchQueryFlow
                .debounce(2000L)
                .distinctUntilChanged(),
            mediaFilterFlow
        ) { query, mediaFilter ->
            query to mediaFilter
        }.distinctUntilChanged()
            .collectLatest { (query, mediaFilter) ->
                requestPosts(query = query, mediaFilter = mediaFilter)
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

    init {
        observeUiSetting()
        initSiteAware()
        observeSearchQuery()
    }

    override fun onRetry() {
        viewModelScope.launch {
            requestPosts(
                query = currentState.searchQuery,
                mediaFilter = currentState.mediaFilter
            )
        }
    }

    override suspend fun reloadSite(site: SelectedSite) {
        /** при смене сайта — сбросить поисковую строку и запросить заново */
        setState { copy(searchQuery = "") }
        searchQueryFlow.value = ""
        requestPosts(query = "", mediaFilter = currentState.mediaFilter)
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.SearchQueryChanged -> onSearchQueryChanged(event.value)
            Event.ToggleHasVideo -> toggleHasVideo()
            Event.ToggleHasAttachments -> toggleHasAttachments()
            Event.ToggleHasImages -> toggleHasImages()
            is Event.NavigateToPost -> navigateToPost(event.post)
            Event.RandomPost -> randomPost()
            Event.SwitchSite -> switchSite()
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        setState { copy(searchQuery = newQuery) }
        searchQueryFlow.value = newQuery
    }

    private fun requestPosts(
        query: String,
        mediaFilter: PostMediaFilter,
    ) {
        val search = query.trim().ifEmpty { null }
        val currentSite = site.value

        val pagingFlow = getSearchPostsPagingUseCase(
            site = currentSite,
            tag = null,
            search = search
        )

        setState {
            copy(
                posts = if (mediaFilter.isActive) {
                    pagingFlow.map { page ->
                        page.filter { post ->
                            post.matchesMediaFilter(mediaFilter)
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    pagingFlow.cachedIn(viewModelScope)
                }
            )
        }
    }

    private fun toggleHasVideo() {
        val current = currentState.mediaFilter
        val next = current.copy(hasVideo = !current.hasVideo)
        mediaFilterFlow.value = next
        setState { copy(mediaFilter = next) }
    }

    private fun toggleHasAttachments() {
        val current = currentState.mediaFilter
        val next = current.copy(hasAttachments = !current.hasAttachments)
        mediaFilterFlow.value = next
        setState { copy(mediaFilter = next) }
    }

    private fun toggleHasImages() {
        val current = currentState.mediaFilter
        val next = current.copy(hasImages = !current.hasImages)
        mediaFilterFlow.value = next
        setState { copy(mediaFilter = next) }
    }

    private fun randomPost() = viewModelScope.launch {
        val postId = getRandomPost()
        navigateToPostDelegate.navigateToPostId(
            service = postId.service,
            userId = postId.artistId,
            postId = postId.postId
        )
    }

    private fun navigateToPost(post: PostDomain) {
        navigateToPostDelegate.navigateToPost(post)
    }
}
