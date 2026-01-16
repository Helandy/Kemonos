package su.afk.kemonos.profile.presenter.setting

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModel
import su.afk.kemonos.preferences.GetCoomerRootUrlUseCase
import su.afk.kemonos.preferences.GetKemonoRootUrlUseCase
import su.afk.kemonos.preferences.siteUrl.ISetBaseUrlsUseCase
import su.afk.kemonos.preferences.ui.CreatorViewMode
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.preferences.useCase.CacheKeys
import su.afk.kemonos.preferences.useCase.CacheTimes
import su.afk.kemonos.preferences.useCase.ICacheTimestampUseCase
import su.afk.kemonos.profile.BuildConfig
import su.afk.kemonos.profile.presenter.setting.delegates.SettingClearCacheDelegate
import su.afk.kemonos.utils.buildBaseUrl
import su.afk.kemonos.utils.normalizeDomain
import su.afk.kemonos.utils.toRootUrl
import javax.inject.Inject

@HiltViewModel
internal class SettingViewModel @Inject constructor(
    private val getCoomerRootUrlUseCase: GetCoomerRootUrlUseCase,
    private val getKemonoRootUrlUseCase: GetKemonoRootUrlUseCase,
    private val cacheTimestamps: ICacheTimestampUseCase,
    private val setBaseUrlsUseCase: ISetBaseUrlsUseCase,
    private val clearCacheDelegate: SettingClearCacheDelegate,
    private val uiSetting: IUiSettingUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage
) : BaseViewModel<SettingState.State>(SettingState.State()) {

    init {
        observeUrls()
        getAppVersion()
        observeCacheTimes()
        setState { copy(loading = false) }

        observeUiSetting()
    }

    fun onInputKemonoDomainChanged(value: String) {
        setState { copy(inputKemonoDomain = normalizeDomain(value)) }
    }

    fun onInputCoomerDomainChanged(value: String) {
        setState { copy(inputCoomerDomain = normalizeDomain(value)) }
    }

    fun onSaveUrls() = viewModelScope.launch {
        setState { copy(isSaving = true, saveSuccess = false) }

        val kemono = buildBaseUrl(state.value.inputKemonoDomain)
        val coomer = buildBaseUrl(state.value.inputCoomerDomain)

        runCatching {
            setBaseUrlsUseCase(kemonoUrl = kemono, coomerUrl = coomer)
        }.onSuccess {
            setState {
                copy(
                    isSaving = false,
                    saveSuccess = true,
                    kemonoUrl = kemono.toRootUrl(),
                    coomerUrl = coomer.toRootUrl()
                )
            }
        }.onFailure { e ->
            setState { copy(isSaving = false, saveSuccess = false) }
            errorHandler.parse(e)
        }
    }

    /** Получение версии */
    private fun getAppVersion() {
        setState { copy(appVersion = BuildConfig.VERSION_NAME) }
    }

    /** Актуальные урлы на сайт (и заполнение инпутов по умолчанию) */
    private fun observeUrls() {
        val kemono = getKemonoRootUrlUseCase()
        val coomer = getCoomerRootUrlUseCase()

        setState {
            copy(
                kemonoUrl = kemono,
                coomerUrl = coomer,
                inputKemonoDomain = state.value.inputKemonoDomain.ifEmpty { normalizeDomain(kemono) },
                inputCoomerDomain = state.value.inputCoomerDomain.ifEmpty { normalizeDomain(coomer) },
            )
        }
    }

    /** Время кэширования */
    private fun observeCacheTimes() {
        setState {
            copy(
                creatorsKemonoCache = cacheTimestamps.cacheTimeUi(CacheKeys.CREATORS_KEMONO, CacheTimes.TTL_7_DAYS),
                creatorsCoomerCache = cacheTimestamps.cacheTimeUi(CacheKeys.CREATORS_COOMER, CacheTimes.TTL_7_DAYS),

                tagsKemonoCache = cacheTimestamps.cacheTimeUi(CacheKeys.TAGS_KEMONO, CacheTimes.TTL_30_DAYS),
                tagsCoomerCache = cacheTimestamps.cacheTimeUi(CacheKeys.TAGS_COOMER, CacheTimes.TTL_30_DAYS),
            )
        }
    }

    fun onClear(action: CacheClearAction) = viewModelScope.launch {
        setState { copy(clearInProgress = true, clearSuccess = null) }

        runCatching {
            clearCacheDelegate.clear(action)
        }.onSuccess {
            observeCacheTimes()
            setState { copy(clearInProgress = false, clearSuccess = true) }
        }.onFailure { e ->
            setState { copy(clearInProgress = false, clearSuccess = false) }
            errorHandler.parse(e)
        }
    }

    /** UI настройки */
    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged()
            .onEach { model ->
                setState { copy(uiSettingModel = model) }
            }
            .launchIn(viewModelScope)
    }

    /** Вид отображения авторов  */
    fun setCreatorsViewMode(value: CreatorViewMode) = viewModelScope.launch {
        uiSetting.setCreatorsViewMode(value)
    }

    /** Debug: пропустить проверку API при входе */
    fun setSkipApiCheckOnLogin(value: Boolean) = viewModelScope.launch {
        uiSetting.setSkipApiCheckOnLogin(value)
    }
}