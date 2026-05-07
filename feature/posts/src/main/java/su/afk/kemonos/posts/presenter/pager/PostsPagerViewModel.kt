package su.afk.kemonos.posts.presenter.pager

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.posts.presenter.pager.model.PostsPage
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.ui.presenter.baseViewModel.getSerializableState
import su.afk.kemonos.ui.presenter.baseViewModel.setSerializableState
import javax.inject.Inject

@HiltViewModel
internal class PostsPagerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage
) : BaseViewModelNew<PostsPagerState.State, PostsPagerState.Event, PostsPagerState.Effect>(savedStateHandle) {

    override fun createInitialState(): PostsPagerState.State =
        PostsPagerState.State(
            currentPage = savedStateHandle.getSerializableState(KEY_CURRENT_PAGE) ?: PostsPage.Popular,
        )

    override fun saveToSavedState(state: PostsPagerState.State) {
        savedStateHandle.setSerializableState(KEY_CURRENT_PAGE, state.currentPage)
    }

    override fun onEvent(event: PostsPagerState.Event) {
        when (event) {
            is PostsPagerState.Event.SetPage -> setPage(event.page)
        }
    }

    private fun setPage(page: PostsPage) {
        if (page == currentState.currentPage) return
        setState { copy(currentPage = page) }
    }

    private companion object {
        const val KEY_CURRENT_PAGE = "posts_pager_current_page"
    }
}
