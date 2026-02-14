package su.afk.kemonos.main.presenter

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import su.afk.kemonos.app.update.api.model.AppUpdateInfo
import su.afk.kemonos.common.crash.ICrashReportManager
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.main.domain.CheckAuthForAllSitesUseCase
import su.afk.kemonos.main.presenter.MainState.*
import su.afk.kemonos.main.presenter.delegates.ApiCheckDelegate
import su.afk.kemonos.main.presenter.delegates.AppUpdateGateDelegate
import su.afk.kemonos.main.presenter.delegates.BaseUrlsObserveDelegate
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.preferences.siteUrl.ISetBaseUrlsUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.storage.api.clear.IClearCacheStorageUseCase
import su.afk.kemonos.utils.url.buildBaseUrl
import su.afk.kemonos.utils.url.normalizeDomain
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
    private val uiSetting: IUiSettingUseCase,
    private val crashReportManager: ICrashReportManager,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {

    override fun createInitialState(): State = State()

    /** Чтобы не запустить стартовую инициализацию дважды */
    private var apiInitStarted = false
    private var startupFlowStarted = false

    init {
        viewModelScope.launch {
            val pendingCrashPath = crashReportManager.latestCrashPath()
            if (pendingCrashPath != null) {
                setState { copy(pendingCrashPath = pendingCrashPath) }
                return@launch
            }
            startStartupFlowIfNeeded()
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.UpdateClick -> onUpdateClick(event.info)
            Event.UpdateLaterClick -> onUpdateLaterClick()
            Event.SaveAndCheck -> onSaveAndCheck()
            Event.SkipCheck -> onSkipCheck()
            is Event.InputKemonoDomainChanged -> {
                setState { copy(inputKemonoDomain = event.value) }
            }

            is Event.InputCoomerDomainChanged -> {
                setState { copy(inputCoomerDomain = event.value) }
            }

            Event.CrashReportDelete -> onCrashReportDelete()
            Event.CrashReportSaveToDevice -> onCrashReportSaveToDevice()
            is Event.CrashReportShared -> {
                crashReportManager.deleteCrash(event.path)
            }

            is Event.CrashReportShareFailed -> {
                setState { copy(pendingCrashPath = event.path) }
            }
        }
    }

    private fun startStartupFlowIfNeeded() {
        if (startupFlowStarted) return
        startupFlowStarted = true

        viewModelScope.launch {
            val uiSettings = uiSetting.prefs.first()

            if (uiSettings.skipApiCheckOnLogin.not()) {
                checkGitHubUpdate()
                clearCacheStorageUseCase.clear()
            } else {
                setState { copy(isLoading = false, apiSuccess = true) }
                navManager.enterTabs()
            }
        }
    }

    /** Проверка версии приложения github */
    private suspend fun checkGitHubUpdate() {
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
                    inputKemonoDomain = currentState.inputKemonoDomain
                        .ifEmpty { normalizeDomain(kemono) },
                    inputCoomerDomain = currentState.inputCoomerDomain
                        .ifEmpty { normalizeDomain(coomer) },
                )
            }
        }
    }

    private fun onUpdateClick(info: AppUpdateInfo) {
        setEffect(Effect.OpenUrl(info.releaseUrl))
    }

    private fun onUpdateLaterClick() {
        setState { copy(updateInfo = null) }
        startApiInitIfNeeded()
    }

    private fun onSaveAndCheck() {
        viewModelScope.launch {
            setBaseUrlsUseCase(
                kemonoUrl = buildBaseUrl(currentState.inputKemonoDomain),
                coomerUrl = buildBaseUrl(currentState.inputCoomerDomain),
            )
            runApiCheck()
        }
    }

    private fun runApiCheck() {
        viewModelScope.launch {
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
    }

    private fun onSkipCheck() {
        setState { copy(isLoading = false, apiSuccess = true, kemonoError = null, coomerError = null) }
        navManager.enterTabs()
    }

    private fun onCrashReportDelete() {
        val crashPath = currentState.pendingCrashPath ?: return
        crashReportManager.deleteCrash(crashPath)
        setState { copy(pendingCrashPath = null) }
        startStartupFlowIfNeeded()
    }

    private fun onCrashReportSaveToDevice() {
        val crashPath = currentState.pendingCrashPath ?: return
        setState { copy(pendingCrashPath = null) }
        setEffect(Effect.SaveCrashReportToDevice(crashPath))
        startStartupFlowIfNeeded()
    }
}
