package su.afk.kemonos.profile.presenter.favoritePosts

import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
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
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.profile.domain.favorites.GetFavoritePostsUseCase
import su.afk.kemonos.profile.domain.favorites.posts.GetFavoritePostsPagingUseCase
import su.afk.kemonos.profile.utils.Const.KEY_SELECT_SITE
import javax.inject.Inject

@HiltViewModel
internal class FavoritePostsViewModel @Inject constructor(
    private val getFavoritePostsUseCase: GetFavoritePostsUseCase,
    private val navManager: NavigationManager,
    private val creatorPostNavigator: ICreatorPostNavigator,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    private val getFavoritePostsPagingUseCase: GetFavoritePostsPagingUseCase,
    private val navigationStorage: NavigationStorage,
    private val uiSetting: IUiSettingUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModel<FavoritePostsState>(FavoritePostsState()) {

    private val searchQueryFlow = MutableStateFlow("")
    private var observeSearchJob: Job? = null

    override fun onRetry() {
        loadSelectedSite()
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
        loadSelectedSite()
    }

    fun onSearchQueryChanged(query: String) {
        searchQueryFlow.value = query
        setState { copy(searchQuery = query) }
    }

    @OptIn(FlowPreview::class)
    private fun startObserveSearchOnce() {
        if (observeSearchJob != null) return

        observeSearchJob = searchQueryFlow
            .debounce(500L)
            .map { it.trim() }
            .distinctUntilChanged()
            .onEach { query ->
                setState {
                    copy(
                        posts = getFavoritePostsPagingUseCase(
                            site = currentState.selectSite,
                            query = query
                        ).cachedIn(viewModelScope)
                    )
                }
            }
            .launchIn(viewModelScope)
    }


    private fun loadSelectedSite() = viewModelScope.launch {
        val selectSite = navigationStorage.consume<SelectedSite>(KEY_SELECT_SITE) ?: SelectedSite.K

        selectedSiteUseCase.setSite(selectSite)
        selectedSiteUseCase.selectedSite.first { it == selectSite }

        setState { copy(selectSite = selectSite) }

        load(refresh = false)

        startObserveSearchOnce()

        searchQueryFlow.value = currentState.searchQuery
    }

    /** Получить избранные посты */
    fun load(refresh: Boolean = false) = viewModelScope.launch {
        setState { copy(loading = true) }

        runCatching {
            getFavoritePostsUseCase(site = currentState.selectSite, refresh = refresh)
        }.onFailure { t ->
            errorHandler.parse(t)
        }

        setState { copy(loading = false) }
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
}
