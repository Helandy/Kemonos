package su.afk.kemonos.error.error.storage

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetryStorage @Inject constructor() {

    private val storage = mutableMapOf<String, () -> Unit>()

    fun put(key: String, action: () -> Unit) {
        storage[key] = action
    }

    /** Забрать и удалить */
    fun consume(key: String): (() -> Unit)? {
        val action = storage[key]
        storage.remove(key)
        return action
    }

    fun remove(key: String) {
        storage.remove(key)
    }
}