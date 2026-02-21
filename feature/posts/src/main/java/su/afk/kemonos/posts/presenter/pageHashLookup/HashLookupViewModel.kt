package su.afk.kemonos.posts.presenter.pageHashLookup

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.posts.domain.usecase.GetHashLookupUseCase
import su.afk.kemonos.posts.presenter.common.NavigateToPostDelegate
import su.afk.kemonos.posts.presenter.pageHashLookup.HashLookupState.*
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.ui.presenter.changeSite.SiteAwareBaseViewModelNew
import javax.inject.Inject

@HiltViewModel
internal class HashLookupViewModel @Inject constructor(
    private val getHashLookupUseCase: GetHashLookupUseCase,
    private val navigateToPostDelegate: NavigateToPostDelegate,
    private val uiSetting: IUiSettingUseCase,
    override val selectedSiteUseCase: ISelectedSiteUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : SiteAwareBaseViewModelNew<State, Event, Effect>() {

    private val hashRegex = Regex("^[a-fA-F0-9]{64}$")

    override fun createInitialState(): State = State()

    init {
        observeUiSetting()
        initSiteAware()
    }

    override suspend fun reloadSite(site: SelectedSite) = Unit

    override fun onEvent(event: Event) {
        when (event) {
            is Event.HashChanged -> onHashChanged(event.value)
            is Event.FileHashDetected -> onFileHashDetected(event.fileName, event.hash)
            Event.Submit -> submit()
            is Event.NavigateToPost -> navigateToPost(event.post)
            Event.SwitchSite -> switchSite()
        }
    }

    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged()
            .onEach { model ->
                setState { copy(uiSettingModel = model) }
            }
            .launchIn(viewModelScope)
    }

    private fun onHashChanged(value: String) {
        setState {
            copy(
                hashInput = value,
                isHashInvalid = false,
                errorMessage = null,
            )
        }
    }

    private fun onFileHashDetected(fileName: String?, hash: String) {
        setState {
            copy(
                hashInput = hash,
                selectedFileName = fileName,
                isHashInvalid = false,
                errorMessage = null,
            )
        }
    }

    private fun submit() {
        val hash = currentState.hashInput.trim().lowercase()
        if (!hashRegex.matches(hash)) {
            setState { copy(isHashInvalid = true) }
            return
        }

        viewModelScope.launch {
            setState {
                copy(
                    isLoading = true,
                    isHashInvalid = false,
                    errorMessage = null,
                )
            }

            runCatching {
                getHashLookupUseCase(hash)
            }.onSuccess { result ->
                val loadStates = LoadStates(
                    refresh = LoadState.NotLoading(endOfPaginationReached = false),
                    prepend = LoadState.NotLoading(endOfPaginationReached = true),
                    append = LoadState.NotLoading(endOfPaginationReached = true),
                )
                setState {
                    copy(
                        hashInput = hash,
                        result = result,
                        posts = flowOf(PagingData.from(result.posts, sourceLoadStates = loadStates)),
                        isLoading = false,
                    )
                }
            }.onFailure { throwable ->
                setState {
                    copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: throwable::class.java.simpleName,
                    )
                }
            }
        }
    }

    private fun navigateToPost(post: PostDomain) {
        navigateToPostDelegate.navigateToPost(post)
    }
}
