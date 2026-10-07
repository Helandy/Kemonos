package su.afk.kemonos.creatorPost.presenter.delegates

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import su.afk.kemonos.creatorPost.presenter.CreatorPostState.Effect
import su.afk.kemonos.creatorPost.presenter.CreatorPostState.State
import su.afk.kemonos.preferences.ui.TranslateTarget
import su.afk.kemonos.ui.translate.TextTranslator
import su.afk.kemonos.ui.translate.preprocessForTranslation
import javax.inject.Inject

internal class TranslateDelegate @Inject constructor(
    private val translator: TextTranslator,
) {
    /** Переключение блока перевода и запуск перевода в выбранном режиме */
    fun onToggleTranslate(
        scope: CoroutineScope,
        getState: () -> State,
        updateState: (State.() -> State) -> Unit,
        sendEffect: (Effect) -> Unit,
    ) {
        val plainText = getState().post?.post?.content?.preprocessForTranslation()
        if (plainText.isNullOrEmpty()) return

        val nextExpanded = !getState().translateExpanded
        updateState { copy(translateExpanded = nextExpanded) }

        if (!nextExpanded) return

        when (getState().uiSettingModel.translateTarget) {
            TranslateTarget.GOOGLE -> {
                updateState { copy(translateExpanded = false) }

                sendEffect(
                    Effect.OpenGoogleTranslate(
                        text = plainText,
                        targetLangTag = getState().uiSettingModel.translateLanguageTag
                    )
                )
                return
            }

            TranslateTarget.APP -> Unit
        }

        if (getState().translateText != null && getState().translateError == null) return
        if (getState().translateLoading) return

        scope.launch {
            updateState { copy(translateLoading = true, translateError = null) }

            runCatching {
                translator.translateAuto(
                    text = plainText,
                    targetLangTag = getState().uiSettingModel.translateLanguageTag
                )
            }.onSuccess { text ->
                updateState { copy(translateText = text, translateLoading = false) }
            }.onFailure { e ->
                updateState {
                    copy(
                        translateLoading = false,
                        translateError = e.message ?: "Translation error"
                    )
                }
            }
        }
    }
}
