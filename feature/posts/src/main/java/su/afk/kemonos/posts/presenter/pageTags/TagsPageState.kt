package su.afk.kemonos.posts.presenter.pageTags

import su.afk.kemonos.posts.api.tags.Tags
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class TagsPageState {
    data class State(
        val loading: Boolean = false,

        /** Все тэги */
        val allTags: List<Tags> = emptyList(),
        val filteredTags: List<Tags> = emptyList(),

        val searchQuery: String = "",
        val uiSettingModel: UiSettingModel = UiSettingModel(),
    ) : UiState

    sealed interface Event : UiEvent {
        data class SearchQueryChanged(val value: String) : Event
        data object PullRefresh : Event
        data class SelectTag(val tag: String?) : Event
        data object SwitchSite : Event
    }

    sealed interface Effect : UiEffect
}
