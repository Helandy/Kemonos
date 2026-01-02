package su.afk.kemonos.posts.presenter.pageTags

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import su.afk.kemonos.api.domain.tags.Tags
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.changeSite.SiteAwareBaseViewModel
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.NavigationStorage
import su.afk.kemonos.posts.domain.usecase.GetAllTagsUseCase
import su.afk.kemonos.posts.navigation.PostsDest
import su.afk.kemonos.posts.presenter.util.Const.KEY_SELECTED_TAG
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import javax.inject.Inject

@HiltViewModel
internal class TagsPageViewModel @Inject constructor(
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val navManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
    override val selectedSiteUseCase: ISelectedSiteUseCase,
) : SiteAwareBaseViewModel<TagsPageState>(
    initialState = TagsPageState()
) {

    init {
        initSiteAware()
    }

    override fun onRetry() {
        viewModelScope.launch {
            load(site = site.value, query = state.value.searchQuery)
        }
    }

    override suspend fun reloadSite(site: SelectedSite) {
        setState { copy(searchQuery = "") }
        load(site = site, query = "")
    }

    fun onSearchQueryChanged(newQuery: String) {
        setState { copy(searchQuery = newQuery) }

        val filtered = filterTags(all = state.value.allTags, query = newQuery)
        setState { copy(tags = filtered) }
    }

    private suspend fun load(site: SelectedSite, query: String) {
        setState { copy(loading = true) }
        val tags = getAllTagsUseCase(site)

        setState {
            copy(
                loading = false,
                allTags = tags,
                tags = filterTags(all = tags, query = query)
            )
        }
    }

    private fun filterTags(all: List<Tags>, query: String): List<Tags> {
        val q = query.trim()
        if (q.isEmpty()) return all
        if (q.length < 2) return all
        return all.filter { it.tags.orEmpty().contains(q, ignoreCase = true) }
    }

    fun navigateToSelectTag(tag: String?) {
        val t = tag.orEmpty()
        setState { copy(selectTag = t) }
        navigationStorage.put(KEY_SELECTED_TAG, t)
        navManager.navigate(PostsDest.TagsSelect)
    }
}