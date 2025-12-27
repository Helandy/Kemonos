package su.afk.kemonos.core.api.domain.useCase

interface CacheTimestampUseCase {
    fun getCacheTimestamp(keyPref: String): Long

    fun updateCacheTimestamp(keyPref: String)

    fun clearCacheTimestamp(keyPref: String)

    fun setCacheTimestamp(keyPref: String, value: Long)
}