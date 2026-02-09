package su.afk.kemonos.posts.presenter.pageTags

import su.afk.kemonos.common.presenter.baseViewModel.UiEffect
import su.afk.kemonos.common.presenter.baseViewModel.UiEvent
import su.afk.kemonos.common.presenter.baseViewModel.UiState
import su.afk.kemonos.posts.api.tags.Tags

internal class TagsPageState {
    data class State(
        val loading: Boolean = false,

        /** Все тэги */
        val allTags: List<Tags> = emptyList(),
        val tags: List<Tags> = emptyList(),
        val selectTag: String? = null,

        val searchQuery: String = "",
    ) : UiState

    sealed interface Event : UiEvent {
        data class SearchQueryChanged(val value: String) : Event
        data class SelectTag(val tag: String?) : Event
        data object SwitchSite : Event
        data object Retry : Event
    }

    sealed interface Effect : UiEffect
}
