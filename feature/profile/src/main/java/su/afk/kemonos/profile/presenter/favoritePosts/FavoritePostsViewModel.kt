package su.afk.kemonos.profile.presenter.favoritePosts

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModel
import su.afk.kemonos.creatorPost.api.ICreatorPostNavigator
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.NavigationStorage
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.profile.domain.favorites.GetFavoritePostsUseCase
import su.afk.kemonos.profile.utils.Const.KEY_SELECT_SITE
import javax.inject.Inject

@HiltViewModel
internal class FavoritePostsViewModel @Inject constructor(
    private val getFavoritePostsUseCase: GetFavoritePostsUseCase,
    private val navManager: NavigationManager,
    private val creatorPostNavigator: ICreatorPostNavigator,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    private val navigationStorage: NavigationStorage,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModel<FavoritePostsState>(FavoritePostsState()) {

    override fun onRetry() {
        setState { copy(loading = true) }
        loadSelectedSite()
    }

    init {
        loadSelectedSite()
    }

    private fun loadSelectedSite() = viewModelScope.launch {
        val selectSite = navigationStorage.consume<SelectedSite>(KEY_SELECT_SITE) ?: SelectedSite.K

        selectedSiteUseCase.setSite(selectSite)
        selectedSiteUseCase.selectedSite.first { it == selectSite }

        setState {
            copy(
                selectSite = selectSite
            )
        }
        load()
    }

    /** Получить избранные посты */
    fun load() = viewModelScope.launch {
        setState { copy(loading = true) }

        val posts = getFavoritePostsUseCase(site = currentState.selectSite).sortedByDescending { it.favedSeq }

        setState {
            copy(
                loading = false,
                allFavoritePosts = posts,
                favoritePosts = posts,
            )
        }
    }

    /** Открытие поста */
    fun navigateToPost(post: PostDomain) {
        navManager.navigate(
            creatorPostNavigator.getCreatorPostDest(
                id = post.userId,
                service = post.service,
                postId = post.id,
                showBarCreator = true
            )
        )
    }

    fun onSearchQueryChanged(query: String) {
        setState { copy(searchQuery = query) }

        val filtered = filterPosts(all = state.value.allFavoritePosts, query = query)
        setState { copy(favoritePosts = filtered) }
    }

    private fun filterPosts(all: List<PostDomain>, query: String): List<PostDomain> {
        val q = query.trim()
        if (q.isEmpty()) return all
        if (q.length < 2) return all

        return all.filter { post ->
            val title = post.title.orEmpty()
            title.contains(q, ignoreCase = true)
        }
    }
}
