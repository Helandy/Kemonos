package su.afk.kemonos.commonscreen.imageViewScreen

import dagger.hilt.android.lifecycle.HiltViewModel
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.commonscreen.imageViewScreen.ImageViewState.*
import su.afk.kemonos.commonscreen.navigator.ImageNavigationConst.KEY_SELECTED_IMAGE
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.storage.NavigationStorage
import javax.inject.Inject

@HiltViewModel
internal class ImageViewViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage
) : BaseViewModelNew<State, Event, Effect>() {

    override fun createInitialState(): State = State()

    override fun onEvent(event: Event) {
        when (event) {
            Event.Back -> navManager.back()
        }
    }

    init {
        val imageUrl = navigationStorage.consume<String>(KEY_SELECTED_IMAGE)

        setState {
            copy(
                imageUrl = imageUrl,
            )
        }
    }

}