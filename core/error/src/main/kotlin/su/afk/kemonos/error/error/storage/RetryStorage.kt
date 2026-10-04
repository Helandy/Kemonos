package su.afk.kemonos.error.error.storage

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetryStorage @Inject constructor() {

    private val storage = ConcurrentHashMap<String, () -> Unit>()

    fun put(key: String, action: () -> Unit) {
        storage[key] = action
    }

    /** Забрать и удалить */
    fun consume(key: String): (() -> Unit)? {
        return storage.remove(key)
    }

    fun remove(key: String) {
        storage.remove(key)
    }
}