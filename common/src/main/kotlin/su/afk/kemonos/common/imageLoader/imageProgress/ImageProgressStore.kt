package su.afk.kemonos.common.imageLoader.imageProgress

import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageProgressStore @Inject constructor() {

    data class Progress(val bytesRead: Long, val contentLength: Long) {
        val percent: Float?
            get() = contentLength.takeIf { it > 0 }?.let { bytesRead.toFloat() / it }
    }

    private val _map = MutableStateFlow<Map<String, Progress>>(emptyMap())
    val map: StateFlow<Map<String, Progress>> = _map

    fun update(key: String, bytesRead: Long, contentLength: Long) {
        _map.update { it + (key to Progress(bytesRead, contentLength)) }
    }

    fun clear(key: String) {
        _map.update { it - key }
    }

    fun flowFor(key: String): Flow<Progress?> =
        map.map { it[key] }
}
