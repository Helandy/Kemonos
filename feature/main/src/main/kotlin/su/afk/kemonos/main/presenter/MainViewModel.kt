package su.afk.kemonos.main.presenter

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import su.afk.kemonos.app.update.api.model.AppUpdateInfo
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModel
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.main.domain.CheckAuthForAllSitesUseCase
import su.afk.kemonos.main.presenter.delegates.ApiCheckDelegate
import su.afk.kemonos.main.presenter.delegates.AppUpdateGateDelegate
import su.afk.kemonos.main.presenter.delegates.BaseUrlsObserveDelegate
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.preferences.siteUrl.ISetBaseUrlsUseCase
import su.afk.kemonos.storage.api.clear.IClearCacheStorageUseCase
import su.afk.kemonos.utils.buildBaseUrl
import su.afk.kemonos.utils.normalizeDomain
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
                        .ifEmpty { normalizeDomain(kemono) },
                    inputCoomerDomain = state.value.inputCoomerDomain
                        .ifEmpty { normalizeDomain(coomer) },
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
        setState { copy(isLoading = true, kemonoError = null, coomerError = null) }

        // 1) если есть cookie — дернем избранное, а если 4xx — cookie очистится
        //    и сайт попадёт в sitesToApiCheck
        val sitesToApiCheck: Set<SelectedSite> = checkAuthForAllSitesUseCase()

        // 2) /posts дергаем только для сайтов, где авторизации нет (или она слетела)
        when (val result = apiCheckDelegate.check(sitesToApiCheck)) {
            ApiCheckDelegate.ApiCheckUiResult.Success -> {
                setState { copy(isLoading = false, apiSuccess = true) }
                navManager.enterTabs()
            }

            is ApiCheckDelegate.ApiCheckUiResult.Failure -> {
                setState {
                    copy(
                        isLoading = false,
                        apiSuccess = false,
                        kemonoError = result.kemonoError,
                        coomerError = result.coomerError,
                    )
                }
            }
        }
    }

    fun onSkipCheck() {
        setState { copy(isLoading = false, apiSuccess = true, kemonoError = null, coomerError = null) }
        navManager.enterTabs()
    }

    fun onInputKemonoDomainChanged(value: String) {
        setState { copy(inputKemonoDomain = value) }
    }

    fun onInputCoomerDomainChanged(value: String) {
        setState { copy(inputCoomerDomain = value) }
    }
}