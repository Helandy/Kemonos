package su.afk.kemonos.navigation

import javax.inject.Inject
import javax.inject.Singleton

/** Переработать в будущем */
@Singleton
class NavigationStorage @Inject constructor() {

    private val storage = mutableMapOf<String, Any>()

    /** Положить объект по ключу */
    fun <T : Any> put(key: String, obj: T) {
        storage[key] = obj
    }

    /**
     * Забрать объект и УДАЛИТЬ его навсегда.
     * Если ключ не найден — вернёт null.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> consume(key: String): T? {
        val value = storage.remove(key) ?: return null
        return value as? T
    }
}