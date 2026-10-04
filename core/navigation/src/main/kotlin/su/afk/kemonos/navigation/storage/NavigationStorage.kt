package su.afk.kemonos.navigation.storage

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Одноразовая in-memory передача тяжёлых объектов между экранами, которые нельзя положить в NavKey
 * (не переживает смерть процесса). Для простых аргументов используйте поля destination.
 */
@Singleton
class NavigationStorage @Inject constructor() {

    private val storage = ConcurrentHashMap<String, Any>()

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