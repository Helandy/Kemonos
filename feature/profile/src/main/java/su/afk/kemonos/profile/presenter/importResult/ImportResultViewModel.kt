package su.afk.kemonos.profile.presenter.importResult

import dagger.hilt.android.lifecycle.HiltViewModel
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.storage.NavigationStorage
import su.afk.kemonos.profile.presenter.importResult.ImportResultState.*
import su.afk.kemonos.profile.utils.Const.KEY_IMPORT_RESULT_PAYLOAD
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import javax.inject.Inject

@HiltViewModel
internal class ImportResultViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {

    override fun createInitialState(): State = State()

    init {
        setState {
            copy(payload = navigationStorage.consume(KEY_IMPORT_RESULT_PAYLOAD))
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            Event.Back -> navigationManager.back()
        }
    }
}
