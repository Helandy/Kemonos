package su.afk.kemonos.preferences.useCase

import su.afk.kemonos.preferences.model.CacheTimeUi

interface ICacheTimestampUseCase {
    fun getCacheTimestamp(keyPref: String): Long

    fun updateCacheTimestamp(keyPref: String)

    fun clearCacheTimestamp(keyPref: String)

    fun setCacheTimestamp(keyPref: String, value: Long)

    /** Получить время кэширования */
    fun cacheTimeUi(key: String, ttlMs: Long): CacheTimeUi
}