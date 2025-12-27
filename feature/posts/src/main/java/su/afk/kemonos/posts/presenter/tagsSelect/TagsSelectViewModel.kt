package su.afk.kemonos.posts.presenter.tagsSelect

import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModel
import su.afk.kemonos.core.api.domain.useCase.ISelectedSiteUseCase
import su.afk.kemonos.core.storage.NavigationStorage
import su.afk.kemonos.domain.domain.models.PostDomain
import su.afk.kemonos.posts.domain.pagingSearch.GetSearchPostsPagingUseCase
import su.afk.kemonos.posts.presenter.common.NavigateToPostDelegate
import su.afk.kemonos.posts.presenter.util.Const.KEY_SELECTED_TAG
import javax.inject.Inject

@HiltViewModel
internal class TagsSelectViewModel @Inject constructor(
    private val selectedSite: ISelectedSiteUseCase,
    private val getSearchPostsPagingUseCase: GetSearchPostsPagingUseCase,
    private val navigateToPostDelegate: NavigateToPostDelegate,
    private val navigationStorage: NavigationStorage,
    private val errorHandlerUseCase: IErrorHandlerUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModel<TagsSelectState>(TagsSelectState()) {

    override fun onRetry() {
        requestPage()
    }

    init {
        val selectTag = navigationStorage.consume<String>(KEY_SELECTED_TAG)
        setState { copy(selectTag = selectTag) }

        requestPage()
    }

    /** Общий метод запроса страницы (suspend, без лишних launch) */
    private fun requestPage() = viewModelScope.launch {
        setState { copy(loading = true) }
        val currentSite = selectedSite.getSite()

        setState {
            copy(
                posts = getSearchPostsPagingUseCase(
                    site = currentSite,
                    tag = currentState.selectTag,
                    search = null,
                ).cachedIn(viewModelScope),
                loading = false,
            )
        }
    }

    fun navigateToPost(post: PostDomain) = navigateToPostDelegate.navigateToPost(post = post)

    fun parseError(t: Throwable) = errorHandlerUseCase.parse(t)
}