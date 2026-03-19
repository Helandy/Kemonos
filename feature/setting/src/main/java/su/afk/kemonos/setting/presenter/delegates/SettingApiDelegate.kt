package su.afk.kemonos.setting.presenter.delegates

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.preferences.siteUrl.ISetBaseUrlsUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.setting.presenter.SettingState
import su.afk.kemonos.utils.url.buildBaseUrl
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
            is SettingState.Event.ApiSetting.InputKemonoDomainChanged ->
                setState { copy(inputKemonoDomain = normalizeDomain(event.value)) }

            is SettingState.Event.ApiSetting.InputCoomerDomainChanged ->
                setState { copy(inputCoomerDomain = normalizeDomain(event.value)) }

            is SettingState.Event.ApiSetting.InputVideoPreviewServerDomainChanged ->
                setState { copy(inputVideoPreviewServerDomain = normalizeDomain(event.value)) }

            SettingState.Event.ApiSetting.SaveUrls -> scope.launch {
                setState { copy(isSaving = true, saveSuccess = false) }

                val s = getState()
                val kemono = buildBaseUrl(s.inputKemonoDomain)
                val coomer = buildBaseUrl(s.inputCoomerDomain)
                val previewDomain = s.inputVideoPreviewServerDomain.ifBlank {
                    normalizeDomain(UiSettingModel.DEFAULT_VIDEO_PREVIEW_SERVER_URL)
                }
                val previewServerUrl = "https://${previewDomain.trim().trim('/')}"

                runCatching {
                    setBaseUrlsUseCase(kemonoUrl = kemono, coomerUrl = coomer)
                    uiSetting.setVideoPreviewServerUrl(previewServerUrl)
                }.onSuccess {
                    setState {
                        copy(
                            isSaving = false,
                            saveSuccess = true,
                            kemonoUrl = kemono.toRootUrl(),
                            coomerUrl = coomer.toRootUrl(),
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
