package su.afk.kemonos.ui.presenter.baseViewModel

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.savedstate.serialization.decodeFromSavedState
import androidx.savedstate.serialization.encodeToSavedState

inline fun <reified T : Any> SavedStateHandle.getSerializableState(key: String): T? {
    val savedState = get<Bundle>(key) ?: return null
    return runCatching { decodeFromSavedState<T>(savedState) }.getOrNull()
}

inline fun <reified T : Any> SavedStateHandle.setSerializableState(key: String, value: T?) {
    if (value == null) {
        remove<Bundle>(key)
    } else {
        set(key, encodeToSavedState(value))
    }
}
