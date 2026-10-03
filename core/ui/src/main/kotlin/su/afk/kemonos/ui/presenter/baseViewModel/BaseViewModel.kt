package su.afk.kemonos.ui.presenter.baseViewModel

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage

interface UiState
interface UiEvent
interface UiEffect

abstract class BaseViewModelNew<S : UiState, E : UiEvent, F : UiEffect>(
    protected val savedStateHandle: SavedStateHandle
) : CoroutineVieModel() {

    protected abstract fun createInitialState(): S

    private val _state by lazy { MutableStateFlow(createInitialState()) }
    val state: StateFlow<S> by lazy { _state.asStateFlow() }

    val currentState: S get() = _state.value
    protected fun setState(reducer: S.() -> S) {
        _state.update {
            val newState = it.reducer()
            saveToSavedState(newState)
            newState
        }
    }

    /**
     * Вызывается при каждом обновлении стейта. 
     * Переопределите, чтобы сохранить нужные поля в [savedStateHandle].
     */
    protected open fun saveToSavedState(state: S) {}

    private val _effect = MutableSharedFlow<F>()
    val effect: SharedFlow<F> = _effect.asSharedFlow()

    protected fun setEffect(effect: F) {
        viewModelScope.launch { _effect.emit(effect) }
    }

    fun setEvent(event: E) = onEvent(event)
    protected abstract fun onEvent(event: E)

    protected abstract val errorHandler: IErrorHandlerUseCase
    protected abstract val retryStorage: RetryStorage

    /** Ключи retry-действий этой VM: чистим при onCleared, чтобы замыкание не держало VM в памяти. */
    private val retryKeys: MutableSet<String> = ConcurrentHashMap.newKeySet()

    override fun onError(exception: Throwable) {
        val retryKey = "${this::class.java.simpleName}:${System.nanoTime()}"

        retryKeys += retryKey
        retryStorage.put(retryKey) { onRetry() }

        errorHandler.parse(exception, navigate = true, retryKey = retryKey)
    }

    protected open fun onRetry() {}

    override fun onCleared() {
        retryKeys.forEach(retryStorage::remove)
        retryKeys.clear()
        super.onCleared()
    }
}
