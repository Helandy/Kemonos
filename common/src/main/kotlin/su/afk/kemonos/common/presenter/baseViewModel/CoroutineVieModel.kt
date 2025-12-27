package su.afk.kemonos.common.presenter.baseViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.SupervisorJob

abstract class CoroutineVieModel : ViewModel() {

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("super", exception.message.orEmpty(), exception)
        onError(exception)
    }

    protected abstract fun onError(exception: Throwable)


    private var viewModelJob = SupervisorJob()
    protected val viewModelScope = CoroutineScope(Main + viewModelJob + handler)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}