package su.afk.kemonos.commonscreen.imageViewScreen

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.imageLoader.imageProgress.ImageProgressStore
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.commonscreen.imageViewScreen.ImageViewState.*
import su.afk.kemonos.commonscreen.navigator.ImageNavigationConst.KEY_SELECTED_IMAGE
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.storage.NavigationStorage
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/** Отдельный экран для просмотра картинки */
@OptIn(FlowPreview::class)
@HiltViewModel
internal class ImageViewViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    private val progressStore: ImageProgressStore,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage
) : BaseViewModelNew<State, Event, Effect>() {

    override fun createInitialState(): State = State()

    override fun onEvent(event: Event) {
        when (event) {
            Event.Back -> {
                progressStore.clear(state.value.requestId)
                navManager.back()
            }

            Event.ImageLoaded -> {
                progressStore.clear(state.value.requestId)
                setState { copy(loading = false, isLoadError = false, errorItem = null) }
            }

            is Event.ImageFailed -> {
                progressStore.clear(state.value.requestId)
                val throwable = event.throwable ?: IllegalStateException("Image loading failed")
                val parsed = errorHandler.parse(throwable, navigate = false)
                setState {
                    copy(
                        loading = false,
                        isLoadError = true,
                        errorItem = parsed,
                    )
                }
            }

            Event.Retry -> {
                progressStore.clear(state.value.requestId)
                setState {
                    copy(
                        loading = true,
                        isLoadError = false,
                        errorItem = null,
                        reloadKey = reloadKey + 1,
                        bytesRead = 0L,
                        contentLength = -1L,
                        progress = 0f,
                    )
                }
            }
        }
    }

    init {
        val imageUrl = navigationStorage.consume<String>(KEY_SELECTED_IMAGE)

        setState {
            copy(
                imageUrl = imageUrl,
                loading = true,
                isLoadError = false,
                errorItem = null,
                bytesRead = 0L,
                contentLength = -1L,
                progress = 0f,
            )
        }

        // Слушаем прогресс именно для текущего url и кладём в state
        viewModelScope.launch {
            val key = state.value.requestId

            progressStore.flowFor(key)
                .filterNotNull()
                .sample(100.milliseconds)
                .map { p ->
                    val percent = (p.percent ?: 0f).coerceIn(0f, 1f)
                    // округляем, чтобы distinct работал (например до 0.5%)
                    val percentStep = (percent * 200).toInt() // 200 шагов = 0.5%
                    val kbStep = (p.bytesRead / 16_384) // шаг 16KB
                    Triple(p, percentStep, kbStep)
                }
                .distinctUntilChanged { old, new ->
                    old.second == new.second && old.third == new.third
                }
                .collect { (p, _, _) ->
                    val percent = (p.percent ?: 0f).coerceIn(0f, 1f)
                    setState {
                        copy(
                            bytesRead = p.bytesRead,
                            contentLength = p.contentLength,
                            progress = percent
                        )
                    }
                }
        }
    }

}
