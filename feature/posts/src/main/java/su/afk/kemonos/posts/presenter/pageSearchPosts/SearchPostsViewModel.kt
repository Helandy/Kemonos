package su.afk.kemonos.posts.presenter.pageSearchPosts

import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.changeSite.SiteAwareBaseViewModel
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.posts.domain.pagingSearch.GetSearchPostsPagingUseCase
import su.afk.kemonos.posts.domain.usecase.GetRandomPost
import su.afk.kemonos.posts.presenter.common.NavigateToPostDelegate
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import javax.inject.Inject

@HiltViewModel
internal class SearchPostsViewModel @Inject constructor(
    private val getSearchPostsPagingUseCase: GetSearchPostsPagingUseCase,
    private val navigateToPostDelegate: NavigateToPostDelegate,
    private val getRandomPost: GetRandomPost,
    override val selectedSiteUseCase: ISelectedSiteUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : SiteAwareBaseViewModel<SearchPostsState>(
    initialState = SearchPostsState()
) {

    /** Flow для текстового поиска */
    private val searchQueryFlow = MutableStateFlow("")

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() = viewModelScope.launch {
        searchQueryFlow
            .debounce(2000L)
            .distinctUntilChanged()
            .collectLatest { query ->
                requestPosts(query = query)
            }
    }

    init {
        initSiteAware()
        observeSearchQuery()
    }

    override fun onRetry() {
        viewModelScope.launch {
            requestPosts(query = state.value.searchQuery)
        }
    }

    override suspend fun reloadSite(site: SelectedSite) {
        /** при смене сайта — сбросить поисковую строку и запросить заново */
        setState { copy(searchQuery = "") }
        searchQueryFlow.value = ""
        requestPosts(query = "")
    }

    fun onSearchQueryChanged(newQuery: String) {
        setState { copy(searchQuery = newQuery) }
        searchQueryFlow.value = newQuery
    }

    private fun requestPosts(query: String) {
        val search = query.trim().ifEmpty { null }
        val currentSite = site.value

        setState {
            copy(
                posts = getSearchPostsPagingUseCase(
                    site = currentSite,
                    tag = null,
                    search = search
                ).cachedIn(viewModelScope)
            )
        }
    }

    fun randomPost() = viewModelScope.launch {
        val postId = getRandomPost()
        navigateToPostDelegate.navigateToPostId(
            service = postId.service,
            userId = postId.artistId,
            postId = postId.postId
        )
    }

    fun navigateToPost(post: PostDomain) = navigateToPostDelegate.navigateToPost(post)

    fun parseError(t: Throwable) = errorHandler.parse(t)
}