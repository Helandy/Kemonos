package su.afk.kemonos.posts.presenter.pager

import dagger.hilt.android.lifecycle.HiltViewModel
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.posts.presenter.pager.model.PostsPage
import javax.inject.Inject

@HiltViewModel
internal class PostsPagerViewModel @Inject constructor(
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage
) : BaseViewModelNew<PostsPagerState.State, PostsPagerState.Event, PostsPagerState.Effect>() {

    override fun createInitialState(): PostsPagerState.State = PostsPagerState.State()

    override fun onEvent(event: PostsPagerState.Event) {
        when (event) {
            is PostsPagerState.Event.SetPage -> setPage(event.page)
        }
    }

    private fun setPage(page: PostsPage) {
        if (page == currentState.currentPage) return
        setState { copy(currentPage = page) }
    }
}
