package su.afk.kemonos.common.presenter.baseViewModel

import kotlinx.coroutines.flow.*
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage

interface UiState
interface UiEvent
interface UiEffect

abstract class BaseViewModelNew<S : UiState, E : UiEvent, F : UiEffect> : CoroutineVieModel() {

    protected abstract fun createInitialState(): S

    private val _state by lazy { MutableStateFlow(createInitialState()) }
    val state: StateFlow<S> = _state.asStateFlow()

    val currentState: S get() = _state.value
    protected fun setState(reducer: S.() -> S) {
        _state.update { it.reducer() }
    }

    private val _effect = MutableSharedFlow<F>()
    val effect: SharedFlow<F> = _effect.asSharedFlow()

    protected fun setEffect(effect: F) {
        _effect.tryEmit(effect)
    }

    fun setEvent(event: E) = onEvent(event)
    protected abstract fun onEvent(event: E)

    protected abstract val errorHandler: IErrorHandlerUseCase
    protected abstract val retryStorage: RetryStorage

    override fun onError(exception: Throwable) {
        val retryKey = "${this::class.java.simpleName}:${System.nanoTime()}"

        retryStorage.put(retryKey) { onRetry() }

        errorHandler.parse(exception, navigate = true, retryKey = retryKey)
    }

    protected open fun onRetry() {}
}

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