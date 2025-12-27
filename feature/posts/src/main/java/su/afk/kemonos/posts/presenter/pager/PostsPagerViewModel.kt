package su.afk.kemonos.posts.presenter.pager

import dagger.hilt.android.lifecycle.HiltViewModel
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModel
import su.afk.kemonos.posts.presenter.pager.model.PostsPage
import javax.inject.Inject

@HiltViewModel
internal class PostsPagerViewModel @Inject constructor(
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage
) : BaseViewModel<PostsPagerState>(
    initialState = PostsPagerState()
) {

    fun onTabClick(page: PostsPage) {
        setState {
            copy(currentPage = page)
        }
    }

    fun onPageChanged(page: PostsPage) {
        if (page != state.value.currentPage) {
            setState {
                copy(currentPage = page)
            }
        }
    }
}