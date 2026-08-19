package su.afk.kemonos.setting.presenter

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.GetRootUrlUseCase
import su.afk.kemonos.preferences.domainResolver.IDomainResolver
import su.afk.kemonos.preferences.siteUrl.IGetBaseUrlsUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.preferences.useCase.CacheKeys
import su.afk.kemonos.preferences.useCase.CacheTimes
import su.afk.kemonos.preferences.useCase.ICacheTimestampUseCase
import su.afk.kemonos.setting.BuildConfig
import su.afk.kemonos.setting.domain.useCase.DeleteDownloadedTranslateModelUseCase
import su.afk.kemonos.setting.domain.useCase.GetDownloadedTranslateModelsUseCase
import su.afk.kemonos.setting.navigation.SettingDestination
import su.afk.kemonos.setting.presenter.delegates.SettingApiDelegate
import su.afk.kemonos.setting.presenter.delegates.SettingCacheDelegate
import su.afk.kemonos.setting.presenter.delegates.SettingUiPreferencesDelegate
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.utils.url.normalizeDomain
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getRootUrlUseCase: GetRootUrlUseCase,
    private val getBaseUrlsUseCase: IGetBaseUrlsUseCase,
    private val domainResolver: IDomainResolver,
    private val cacheTimestamps: ICacheTimestampUseCase,
    private val uiSetting: IUiSettingUseCase,
    private val uiPrefsDelegate: SettingUiPreferencesDelegate,
    private val apiDelegate: SettingApiDelegate,
    private val cacheDelegate: SettingCacheDelegate,
    private val getDownloadedTranslateModelsUseCase: GetDownloadedTranslateModelsUseCase,
    private val deleteDownloadedTranslateModelUseCase: DeleteDownloadedTranslateModelUseCase,
    private val navManager: NavigationManager,
    savedStateHandle: SavedStateHandle,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage
) : BaseViewModelNew<SettingState.State, SettingState.Event, SettingState.Effect>(savedStateHandle) {

    override fun createInitialState(): SettingState.State = SettingState.State()

    override fun onEvent(event: SettingState.Event) {
        when (event) {
            SettingState.Event.Back -> navManager.back()
            SettingState.Event.OpenCreatorTabsOrderEditor -> navManager.navigate(SettingDestination.CreatorTabsOrder)
            SettingState.Event.OpenUiSettings -> navManager.navigate(SettingDestination.Ui)
            SettingState.Event.OpenVideoSettings -> navManager.navigate(SettingDestination.Video)
            SettingState.Event.OpenTranslateSettings -> navManager.navigate(SettingDestination.Translate)
            SettingState.Event.OpenTranslateModels -> navManager.navigate(SettingDestination.TranslateModels)
            SettingState.Event.OpenNetworkSettings -> navManager.navigate(SettingDestination.Network)
            SettingState.Event.OpenDatabaseSettings -> navManager.navigate(SettingDestination.Database)
            SettingState.Event.OpenDownloadSettings -> navManager.navigate(SettingDestination.Downloads)
            SettingState.Event.OpenHelpImportSettings -> navManager.navigate(SettingDestination.HelpImport)
            SettingState.Event.OpenDebugStorageSettings -> navManager.navigate(SettingDestination.DebugStorage)
            SettingState.Event.RefreshTranslateModels -> refreshTranslateModels()
            is SettingState.Event.DeleteTranslateModel -> deleteTranslateModel(event.modelId)

            is SettingState.Event.ChangeViewSetting ->
                uiPrefsDelegate.handle(event, viewModelScope)

            is SettingState.Event.ApiSetting ->
                apiDelegate.handle(
                    event = event,
                    scope = viewModelScope,
                    getState = { state.value },
                    setState = ::setState,
                )

            is SettingState.Event.CacheClearAction ->
                cacheDelegate.handle(
                    event = event,
                    scope = viewModelScope,
                    setState = ::setState,
                    onAfterSuccess = ::observeCacheTimes,
                )
        }
    }


    init {
        observeUrls()
        getAppVersion()
        observeCacheTimes()
        setState { copy(loading = false) }

        observeUiSetting()
    }

    /** Получение версии */
    private fun getAppVersion() {
        setState { copy(appVersion = BuildConfig.VERSION_NAME) }
    }

    /** Актуальные урлы на сайт (и заполнение инпутов по умолчанию) */
    private fun observeUrls() {
        val rootUrls = SelectedSite.entries.associateWith(getRootUrlUseCase::invoke)
        val hosts = SelectedSite.entries.associateWith(domainResolver::hostConfig)
        val imageOverrides = SelectedSite.entries.associateWith {
            getBaseUrlsUseCase.imageHostOverride(it).value
        }
        val fileOverrides = SelectedSite.entries.associateWith {
            getBaseUrlsUseCase.fileHostOverride(it).value
        }

        setState {
            copy(
                siteUrls = rootUrls,
                imageHostUrls = hosts.mapValues { (_, h) -> h.imageBaseUrl },
                fileHostUrls = hosts.mapValues { (_, h) -> h.fileBaseUrl },
                /** Поле, которого пользователь ещё не касался, подставляем из prefs. */
                inputDomains = rootUrls.mapValues { (site, url) ->
                    inputDomains[site].orEmpty().ifEmpty { normalizeDomain(url) }
                },
                inputImageHostOverrides = imageOverrides.mapValues { (site, override) ->
                    inputImageHostOverrides[site].orEmpty().ifEmpty { normalizeDomain(override) }
                },
                inputFileHostOverrides = fileOverrides.mapValues { (site, override) ->
                    inputFileHostOverrides[site].orEmpty().ifEmpty { normalizeDomain(override) }
                },
                inputVideoPreviewServerDomain = state.value.inputVideoPreviewServerDomain.ifEmpty {
                    normalizeDomain(state.value.uiSettingModel.videoPreviewServerUrl)
                },
            )
        }
    }

    /** Время кэширования */
    private fun observeCacheTimes() {
        setState {
            copy(
                creatorsCache = SelectedSite.entries.associateWith { site ->
                    cacheTimestamps.cacheTimeUi(CacheKeys.creators(site), CacheTimes.TTL_7_DAYS)
                },
                tagsCache = SelectedSite.entries.associateWith { site ->
                    cacheTimestamps.cacheTimeUi(CacheKeys.tags(site), CacheTimes.TTL_30_DAYS)
                },
            )
        }
    }

    /** UI настройки */
    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged()
            .onEach { model ->
                setState {
                    copy(
                        uiSettingModel = model,
                        inputVideoPreviewServerDomain = inputVideoPreviewServerDomain.ifEmpty {
                            normalizeDomain(model.videoPreviewServerUrl)
                        },
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun refreshTranslateModels() {
        viewModelScope.launch {
            setState { copy(translateModelsLoading = true) }
            try {
                val models = getDownloadedTranslateModelsUseCase()
                setState {
                    copy(
                        translateModels = models,
                        deletingTranslateModelId = null,
                    )
                }
            } finally {
                setState {
                    copy(
                        translateModelsLoading = false,
                        deletingTranslateModelId = null,
                    )
                }
            }
        }
    }

    private fun deleteTranslateModel(modelId: String) {
        viewModelScope.launch {
            setState { copy(deletingTranslateModelId = modelId) }
            try {
                deleteDownloadedTranslateModelUseCase(modelId)
            } finally {
                refreshTranslateModels()
            }
        }
    }
}
