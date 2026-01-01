package su.afk.kemonos.main.presenter

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import su.afk.kemonos.app.update.api.model.AppUpdateInfo
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModel
import su.afk.kemonos.core.api.domain.useCase.ISetBaseUrlsUseCase
import su.afk.kemonos.main.domain.CheckAuthForAllSitesUseCase
import su.afk.kemonos.main.presenter.delegates.ApiCheckDelegate
import su.afk.kemonos.main.presenter.delegates.AppUpdateGateDelegate
import su.afk.kemonos.main.presenter.delegates.BaseUrlsObserveDelegate
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.storage.api.clear.IClearCacheStorageUseCase
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val setBaseUrlsUseCase: ISetBaseUrlsUseCase,
    private val checkAuthForAllSitesUseCase: CheckAuthForAllSitesUseCase,
    private val clearCacheStorageUseCase: IClearCacheStorageUseCase,
    private val apiCheckDelegate: ApiCheckDelegate,
    private val updateGateDelegate: AppUpdateGateDelegate,
    private val baseUrlsObserveDelegate: BaseUrlsObserveDelegate,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModel<MainState.State>(MainState.State()) {

    private val _effect = MutableSharedFlow<MainState.MainEffect>()
    val effect = _effect.asSharedFlow()

    /** Чтобы не запустить стартовую инициализацию дважды */
    private var apiInitStarted = false

    init {
        viewModelScope.launch {
            gateUpdateThenInit()
            clearCacheStorageUseCase.clear()
        }
    }

    private suspend fun gateUpdateThenInit() {
        val updateInfo = updateGateDelegate.check()
        if (updateInfo != null) {
            setState { copy(updateInfo = updateInfo) }
            return
        }
        startApiInitIfNeeded()
    }

    private fun startApiInitIfNeeded() {
        if (apiInitStarted) return
        apiInitStarted = true

        observeBaseUrls()
        runApiCheck()
    }

    private fun observeBaseUrls() {
        baseUrlsObserveDelegate.observe(viewModelScope) { kemono, coomer ->
            setState {
                copy(
                    kemonoUrl = kemono,
                    coomerUrl = coomer,
                    inputKemonoDomain = state.value.inputKemonoDomain
                        .ifEmpty { baseUrlsObserveDelegate.extractDomain(kemono) },
                    inputCoomerDomain = state.value.inputCoomerDomain
                        .ifEmpty { baseUrlsObserveDelegate.extractDomain(coomer) },
                )
            }
        }
    }

    fun onUpdateClick(info: AppUpdateInfo) = viewModelScope.launch {
        _effect.emit(MainState.MainEffect.OpenUrl(info.releaseUrl))
    }

    fun onUpdateLaterClick() {
        setState { copy(updateInfo = null) }
        startApiInitIfNeeded()
    }

    fun onSaveAndCheck() = viewModelScope.launch {
        setBaseUrlsUseCase(
            kemonoUrl = buildBaseUrl(state.value.inputKemonoDomain),
            coomerUrl = buildBaseUrl(state.value.inputCoomerDomain),
        )
        runApiCheck()
    }

    private fun runApiCheck() = viewModelScope.launch {
        setState { copy(isLoading = true, error = null) }

        when (val result = apiCheckDelegate.check()) {
            ApiCheckDelegate.ApiCheckUiResult.Success -> {
                checkAuthForAllSitesUseCase()
                setState { copy(isLoading = false, apiSuccess = true) }
                navManager.enterTabs()
            }

            is ApiCheckDelegate.ApiCheckUiResult.Failure -> {
                setState { copy(isLoading = false, apiSuccess = false, error = result.error) }
            }
        }
    }

    fun onSkipCheck() {
        setState { copy(isLoading = false, apiSuccess = true, error = null) }
        navManager.enterTabs()
    }

    fun onInputKemonoDomainChanged(value: String) {
        setState { copy(inputKemonoDomain = value) }
    }

    fun onInputCoomerDomainChanged(value: String) {
        setState { copy(inputCoomerDomain = value) }
    }

    /** util: домен -> base api url */
    private fun buildBaseUrl(domain: String): String =
        "https://${domain.trim().trim('/')}/api/"
}