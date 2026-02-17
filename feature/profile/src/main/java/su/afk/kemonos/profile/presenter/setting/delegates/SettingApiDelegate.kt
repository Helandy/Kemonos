package su.afk.kemonos.profile.presenter.setting.delegates

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.preferences.siteUrl.ISetBaseUrlsUseCase
import su.afk.kemonos.profile.presenter.setting.SettingState.Event
import su.afk.kemonos.profile.presenter.setting.SettingState.State
import su.afk.kemonos.utils.url.buildBaseUrl
import su.afk.kemonos.utils.url.normalizeDomain
import su.afk.kemonos.utils.url.toRootUrl
import javax.inject.Inject

internal class SettingApiDelegate @Inject constructor(
    private val setBaseUrlsUseCase: ISetBaseUrlsUseCase,
    private val errorHandler: IErrorHandlerUseCase,
) {
    fun handle(
        event: Event.ApiSetting,
        scope: CoroutineScope,
        getState: () -> State,
        setState: (State.() -> State) -> Unit,
    ) {
        when (event) {
            is Event.ApiSetting.InputKemonoDomainChanged ->
                setState { copy(inputKemonoDomain = normalizeDomain(event.value)) }

            is Event.ApiSetting.InputCoomerDomainChanged ->
                setState { copy(inputCoomerDomain = normalizeDomain(event.value)) }

            Event.ApiSetting.SaveUrls -> scope.launch {
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

