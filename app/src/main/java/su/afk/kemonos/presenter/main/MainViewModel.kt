package su.afk.kemonos.presenter.main

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import su.afk.kemonos.app.update.api.model.AppUpdateInfo
import su.afk.kemonos.app.update.api.useCase.ICheckAppUpdateUseCase
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModel
import su.afk.kemonos.common.repository.checkApi.ICheckApiRepository
import su.afk.kemonos.core.api.domain.useCase.IGetBaseUrlsUseCase
import su.afk.kemonos.core.api.domain.useCase.ISetBaseUrlsUseCase
import su.afk.kemonos.domain.CheckAuthForAllSitesUseCase
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.storage.api.clear.IClearCacheStorageUseCase
import javax.inject.Inject

/** перекинуть в отдельную фичу */
@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val repository: ICheckApiRepository,
    private val setBaseUrlsUseCase: ISetBaseUrlsUseCase,
    private val getBaseUrlsUseCase: IGetBaseUrlsUseCase,
    private val checkAppUpdateUseCase: ICheckAppUpdateUseCase,
    private val checkAuthForAllSitesUseCase: CheckAuthForAllSitesUseCase,
    private val clearCacheStorageUseCase: IClearCacheStorageUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModel<MainState>(MainState()) {

    private val _effect = MutableSharedFlow<MainEffect>()
    val effect = _effect.asSharedFlow()

    /** Чтобы не запустить стартовую инициализацию дважды */
    private var apiInitStarted = false

    init {
        viewModelScope.launch {
            checkUpdateAndInit()
            clearCacheStorageUseCase.clear()
        }
    }

    /** Проверка обновления */
    private suspend fun checkUpdateAndInit() {
        val updateInfo = checkAppUpdateUseCase()

        if (updateInfo != null) {
            setState { copy(updateInfo = updateInfo) }
            return
        }
        startApiInitIfNeeded()
    }

    /** Запускаем наблюдение за URL'ами + проверку API (только один раз) */
    private fun startApiInitIfNeeded() {
        if (apiInitStarted) return
        apiInitStarted = true

        viewModelScope.launch {
            combine(
                getBaseUrlsUseCase.kemonoUrl,
                getBaseUrlsUseCase.coomerUrl,
            ) { kemono, coomer ->
                kemono to coomer
            }.collect { (kemono, coomer) ->
                setState {
                    copy(
                        kemonoUrl = kemono,
                        coomerUrl = coomer,
                        inputKemonoDomain = state.value.inputKemonoDomain.ifEmpty { extractDomain(kemono) },
                        inputCoomerDomain = state.value.inputCoomerDomain.ifEmpty { extractDomain(coomer) },
                    )
                }
            }
        }

        /** проверка API */
        runApiCheck()
    }

    /** Пользователь нажал "Обновиться" */
    fun onUpdateClick(info: AppUpdateInfo) = viewModelScope.launch {
        _effect.emit(MainEffect.OpenUrl(info.releaseUrl))
    }

    /** Пользователь нажал "В следующий раз" */
    fun onUpdateLaterClick() {
        setState { copy(updateInfo = null) }
        startApiInitIfNeeded()
    }

    /** Кнопка Проверить -> сохраняем URLs и проверяем */
    fun onSaveAndCheck() = viewModelScope.launch {
        setBaseUrlsUseCase(
            kemonoUrl = buildBaseUrl(state.value.inputKemonoDomain),
            coomerUrl = buildBaseUrl(state.value.inputCoomerDomain),
        )
        runApiCheck()
    }

    private fun buildBaseUrl(domain: String): String =
        "https://${domain.trim().trim('/')}/api/"

    /** Проверка api */
    private fun runApiCheck() = viewModelScope.launch {
        setState { copy(isLoading = true, error = null) }

        val check = repository.getApiCheck()
        if (check.success) {
            /** Проверка авторизации */
            checkAuthForAllSitesUseCase()

            setState { copy(isLoading = false, apiSuccess = true) }
            navManager.enterTabs()
        } else {
            setState {
                copy(
                    isLoading = false,
                    apiSuccess = false,
                    error = check.error,
                )
            }
        }
    }

    /** Считаем проверку пройденной */
    fun onSkipCheck() {
        setState {
            copy(
                isLoading = false,
                apiSuccess = true,
                error = null,
            )
        }
        navManager.enterTabs()
    }

    private fun extractDomain(baseUrl: String): String =
        baseUrl
            .removePrefix("https://")
            .removePrefix("http://")
            .removeSuffix("/api/")
            .removeSuffix("/api")
            .trim('/')

    fun onInputKemonoDomainChanged(value: String) {
        setState { copy(inputKemonoDomain = value) }
    }

    fun onInputCoomerDomainChanged(value: String) {
        setState { copy(inputCoomerDomain = value) }
    }
}
