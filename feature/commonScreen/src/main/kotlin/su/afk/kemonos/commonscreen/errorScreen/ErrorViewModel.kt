package su.afk.kemonos.commonscreen.errorScreen

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.commonscreen.navigator.CommonScreenDest
import su.afk.kemonos.navigation.NavigationManager

internal class ErrorViewModel @AssistedInject constructor(
    @Assisted private val dest: CommonScreenDest.ErrorNavigatorDest,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
    private val navManager: NavigationManager,
) : BaseViewModelNew<ErrorScreenState.State, ErrorScreenState.Event, ErrorScreenState.Effect>() {

    override fun createInitialState(): ErrorScreenState.State = ErrorScreenState.State()

    @AssistedFactory
    interface Factory {
        fun create(dest: CommonScreenDest.ErrorNavigatorDest): ErrorViewModel
    }

    init {
        setState { copy(error = dest.error) }
    }

    override fun onEvent(event: ErrorScreenState.Event) {
        when (event) {
            ErrorScreenState.Event.Retry -> retry()
            ErrorScreenState.Event.Back -> back()
        }
    }

    private fun retry() {
        val key = currentState.error?.retryKey ?: return
        retryStorage.consume(key)?.invoke()
        navManager.back()
    }

    private fun back() = navManager.backTwo()

    override fun onCleared() {
        dest.error.retryKey?.let(retryStorage::remove)
        super.onCleared()
    }
}
