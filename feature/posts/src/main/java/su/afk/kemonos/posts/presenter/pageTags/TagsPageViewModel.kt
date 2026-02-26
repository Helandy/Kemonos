package su.afk.kemonos.posts.presenter.pageTags

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.storage.NavigationStorage
import su.afk.kemonos.posts.api.tags.Tags
import su.afk.kemonos.posts.domain.usecase.GetAllTagsUseCase
import su.afk.kemonos.posts.navigation.PostsDest
import su.afk.kemonos.posts.presenter.pageTags.TagsPageState.*
import su.afk.kemonos.posts.presenter.util.Const.KEY_SELECTED_TAG
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.ui.presenter.changeSite.SiteAwareBaseViewModelNew
import javax.inject.Inject

@HiltViewModel
internal class TagsPageViewModel @Inject constructor(
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val navManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    override val selectedSiteUseCase: ISelectedSiteUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : SiteAwareBaseViewModelNew<State, Event, Effect>() {

    override fun createInitialState(): State = State()

    override fun onRetry() {
        onEvent(Event.Retry)
    }

    init {
        initSiteAware()
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.SearchQueryChanged -> onSearchQueryChanged(event.value)
            is Event.SelectTag -> navigateToSelectTag(event.tag)
            Event.SwitchSite -> switchSite()
            Event.Retry -> viewModelScope.launch {
                load(site = site.value)
            }
        }
    }

    override suspend fun reloadSite(site: SelectedSite) {
        setState { copy(searchQuery = "") }
        load(site = site)
    }

    private fun onSearchQueryChanged(newQuery: String) {
        setState { copy(searchQuery = newQuery) }

        val filtered = filterTags(all = currentState.allTags, query = newQuery)
        setState { copy(filteredTags = filtered) }
    }

    private suspend fun load(site: SelectedSite) {
        setState { copy(loading = true) }
        val loadedTags = getAllTagsUseCase(site)

        setState {
            copy(
                loading = false,
                allTags = loadedTags,
                filteredTags = filterTags(all = loadedTags, query = searchQuery)
            )
        }
    }

    private fun filterTags(all: List<Tags>, query: String): List<Tags> {
        val q = query.trim()
        if (q.isEmpty()) return all
        if (q.length < 2) return all
        return all.filter { it.tags.orEmpty().contains(q, ignoreCase = true) }
    }

    private fun navigateToSelectTag(tag: String?) {
        setState { copy(selectTag = tag.orEmpty()) }
        navigationStorage.put(KEY_SELECTED_TAG, tag.orEmpty())
        navManager.navigate(PostsDest.TagsSelect)
    }
}
