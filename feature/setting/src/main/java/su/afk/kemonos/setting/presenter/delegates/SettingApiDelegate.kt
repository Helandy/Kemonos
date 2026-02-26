package su.afk.kemonos.setting.presenter.delegates

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.preferences.siteUrl.ISetBaseUrlsUseCase
import su.afk.kemonos.setting.presenter.SettingState
import su.afk.kemonos.utils.url.buildBaseUrl
import su.afk.kemonos.utils.url.normalizeDomain
import su.afk.kemonos.utils.url.toRootUrl
import javax.inject.Inject

class SettingApiDelegate @Inject constructor(
    private val setBaseUrlsUseCase: ISetBaseUrlsUseCase,
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

            SettingState.Event.ApiSetting.SaveUrls -> scope.launch {
                setState { copy(isSaving = true, saveSuccess = false) }

                val s = getState()
                val kemono = buildBaseUrl(s.inputKemonoDomain)
                val coomer = buildBaseUrl(s.inputCoomerDomain)

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
        }
    }
}
