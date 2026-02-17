package su.afk.kemonos.posts.presenter.pager

import su.afk.kemonos.posts.presenter.pager.model.PostsPage
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class PostsPagerState {
    data class State(
        val currentPage: PostsPage = PostsPage.Popular,
    ) : UiState

    sealed interface Event : UiEvent {
        data class SetPage(val page: PostsPage) : Event
    }

    sealed interface Effect : UiEffect
}
