package su.afk.kemonos.setting.presenter.delegates

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.preferences.domainResolver.SiteHostConfig
import su.afk.kemonos.preferences.domainResolver.SiteHostConfigResolver
import su.afk.kemonos.preferences.siteUrl.ISetBaseUrlsUseCase
import su.afk.kemonos.preferences.siteUrl.SiteUrlUpdate
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.setting.presenter.SettingState
import su.afk.kemonos.utils.url.buildBaseUrl
import su.afk.kemonos.utils.url.buildRootUrl
import su.afk.kemonos.utils.url.normalizeDomain
import su.afk.kemonos.utils.url.toRootUrl
import javax.inject.Inject

class SettingApiDelegate @Inject constructor(
    private val setBaseUrlsUseCase: ISetBaseUrlsUseCase,
    private val uiSetting: IUiSettingUseCase,
    private val errorHandler: IErrorHandlerUseCase,
) {
    fun handle(
        event: SettingState.Event.ApiSetting,
        scope: CoroutineScope,
        getState: () -> SettingState.State,
        setState: (SettingState.State.() -> SettingState.State) -> Unit,
    ) {
        when (event) {
            is SettingState.Event.ApiSetting.InputDomainChanged -> setState {
                copy(inputDomains = inputDomains + (event.site to normalizeDomain(event.value)))
            }

            is SettingState.Event.ApiSetting.InputImageHostChanged -> setState {
                copy(
                    inputImageHostOverrides = inputImageHostOverrides +
                        (event.site to normalizeDomain(event.value)),
                )
            }

            is SettingState.Event.ApiSetting.InputFileHostChanged -> setState {
                copy(
                    inputFileHostOverrides = inputFileHostOverrides +
                        (event.site to normalizeDomain(event.value)),
                )
            }

            is SettingState.Event.ApiSetting.InputVideoPreviewServerDomainChanged ->
                setState { copy(inputVideoPreviewServerDomain = normalizeDomain(event.value)) }

            is SettingState.Event.ApiSetting.ToggleApiSite -> scope.launch {
                val current = getState().uiSettingModel.enabledSites
                val next = if (event.enabled) current + event.site else current - event.site
                if (next.isNotEmpty()) {
                    uiSetting.setEnabledSites(next)
                }
            }

            SettingState.Event.ApiSetting.SaveUrls -> scope.launch {
                setState { copy(isSaving = true, saveSuccess = false) }

                val s = getState()

                val updates: Map<SelectedSite, SiteUrlUpdate> =
                    SelectedSite.entries.associateWith { site ->
                        SiteUrlUpdate(
                            apiUrl = buildBaseUrl(s.inputDomain(site)),
                            imageHostOverride = buildRootUrl(s.inputImageHostOverrides[site].orEmpty()),
                            fileHostOverride = buildRootUrl(s.inputFileHostOverrides[site].orEmpty()),
                        )
                    }

                val resolvedHosts: Map<SelectedSite, SiteHostConfig> =
                    updates.mapValues { (site, update) ->
                        SiteHostConfigResolver.resolve(
                            site = site,
                            apiBaseUrl = update.apiUrl,
                            imageHostOverride = update.imageHostOverride.orEmpty(),
                            fileHostOverride = update.fileHostOverride.orEmpty(),
                        )
                    }

                val previewDomain = s.inputVideoPreviewServerDomain.ifBlank {
                    normalizeDomain(UiSettingModel.DEFAULT_VIDEO_PREVIEW_SERVER_URL)
                }
                val previewServerUrl = "https://${previewDomain.trim().trim('/')}"

                runCatching {
                    setBaseUrlsUseCase(updates)
                    uiSetting.setEnabledSites(s.uiSettingModel.enabledSites)
                    uiSetting.setVideoPreviewServerUrl(previewServerUrl)
                }.onSuccess {
                    setState {
                        copy(
                            isSaving = false,
                            saveSuccess = true,
                            siteUrls = updates.mapValues { (_, u) -> u.apiUrl.toRootUrl() },
                            imageHostUrls = resolvedHosts.mapValues { (_, h) -> h.imageBaseUrl },
                            fileHostUrls = resolvedHosts.mapValues { (_, h) -> h.fileBaseUrl },
                            inputVideoPreviewServerDomain = previewDomain,
                        )
                    }
                }.onFailure { e ->
                    setState { copy(isSaving = false, saveSuccess = false) }
                    errorHandler.parse(e)
                }
            }
        }
    }
}
