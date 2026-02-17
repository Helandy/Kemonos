package su.afk.kemonos.posts.presenter.tagsSelect

import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.storage.NavigationStorage
import su.afk.kemonos.posts.domain.pagingSearch.GetSearchPostsPagingUseCase
import su.afk.kemonos.posts.presenter.common.NavigateToPostDelegate
import su.afk.kemonos.posts.presenter.tagsSelect.TagsSelectState.*
import su.afk.kemonos.posts.presenter.util.Const.KEY_SELECTED_TAG
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import javax.inject.Inject

@HiltViewModel
internal class TagsSelectViewModel @Inject constructor(
    private val selectedSite: ISelectedSiteUseCase,
    private val getSearchPostsPagingUseCase: GetSearchPostsPagingUseCase,
    private val navigateToPostDelegate: NavigateToPostDelegate,
    private val navigationStorage: NavigationStorage,
    private val uiSetting: IUiSettingUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {

    override fun createInitialState(): State = State()

    override fun onRetry() {
        requestPage()
    }

    /** UI настройки */
    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged()
            .onEach { model ->
                setState { copy(uiSettingModel = model) }
            }
            .launchIn(viewModelScope)
    }

    init {
        observeUiSetting()
        val selectTag = navigationStorage.consume<String>(KEY_SELECTED_TAG)
        setState { copy(selectTag = selectTag) }

        requestPage()
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.NavigateToPost -> navigateToPost(event.post)
        }
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

    private fun navigateToPost(post: PostDomain) {
        navigateToPostDelegate.navigateToPost(post = post)
    }
}
