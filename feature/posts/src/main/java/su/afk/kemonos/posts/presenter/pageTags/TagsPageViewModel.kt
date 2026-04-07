package su.afk.kemonos.posts.presenter.pageTags

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.storage.NavigationStorage
import su.afk.kemonos.posts.api.tags.Tags
import su.afk.kemonos.posts.domain.usecase.GetAllTagsUseCase
import su.afk.kemonos.posts.navigation.PostsDestination
import su.afk.kemonos.posts.presenter.pageTags.TagsPageState.*
import su.afk.kemonos.posts.util.Const.TAGS_SELECTED_NAV_KEY
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.ui.presenter.changeSite.SiteAwareBaseViewModelNew
import javax.inject.Inject

@HiltViewModel
internal class TagsPageViewModel @Inject constructor(
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val navManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    private val uiSetting: IUiSettingUseCase,
    override val selectedSiteUseCase: ISelectedSiteUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : SiteAwareBaseViewModelNew<State, Event, Effect>() {

    override fun createInitialState(): State = State()

    override fun onRetry() {
        viewModelScope.launch {
            load(site = site.value, forceRefresh = true)
        }
    }

    init {
        observeUiSetting()
        initSiteAware()
    }

    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged()
            .onEach { model ->
                setState { copy(uiSettingModel = model) }
            }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.SearchQueryChanged -> onSearchQueryChanged(event.value)
            Event.PullRefresh -> onPullRefresh()
            is Event.SelectTag -> navigateToSelectTag(event.tag)
            Event.SwitchSite -> switchSite()
        }
    }

    override suspend fun reloadSite(site: SelectedSite) {
        setState { copy(searchQuery = "") }
        load(site = site, forceRefresh = false)
    }

    private fun onSearchQueryChanged(newQuery: String) {
        val filtered = filterTags(all = currentState.allTags, query = newQuery)
        setState {
            copy(
                searchQuery = newQuery,
                filteredTags = filtered,
            )
        }
    }

    private suspend fun load(site: SelectedSite, forceRefresh: Boolean) {
        setState { copy(loading = true) }

        val loadedTags = try {
            getAllTagsUseCase(site = site, forceRefresh = forceRefresh)
        } finally {
            setState { copy(loading = false) }
        }

        setState {
            copy(
                allTags = loadedTags,
                filteredTags = filterTags(all = loadedTags, query = searchQuery),
            )
        }
    }

    private fun onPullRefresh() {
        viewModelScope.launch {
            load(site = site.value, forceRefresh = true)
        }
    }

    private fun filterTags(all: List<Tags>, query: String): List<Tags> {
        val q = query.trim()
        if (q.length < MIN_QUERY_LENGTH) return all
        return all.filter { tag ->
            tag.tags?.contains(q, ignoreCase = true) == true
        }
    }

    private fun navigateToSelectTag(tag: String?) {
        val normalizedTag = tag?.trim()?.ifEmpty { null } ?: return
        navigationStorage.put(TAGS_SELECTED_NAV_KEY, normalizedTag)
        navManager.navigate(PostsDestination.TagsSelect)
    }

    companion object {
        const val MIN_QUERY_LENGTH = 2
    }
}
