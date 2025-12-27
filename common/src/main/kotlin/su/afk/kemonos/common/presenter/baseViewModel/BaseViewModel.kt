package su.afk.kemonos.common.presenter.baseViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage

abstract class BaseViewModel<S>(
    initialState: S,
) : CoroutineVieModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state
    val currentState: S get() = state.value

    protected fun setState(reducer: S.() -> S) {
        _state.update { it.reducer() }
    }

    protected abstract val errorHandler: IErrorHandlerUseCase
    protected abstract val retryStorage: RetryStorage

    override fun onError(exception: Throwable) {
        val retryKey = "${this::class.java.simpleName}:${System.nanoTime()}"

        retryStorage.put(retryKey) { onRetry() }

        errorHandler.parse(exception, navigate = true, retryKey = retryKey)
    }

    protected open fun onRetry() {}
}