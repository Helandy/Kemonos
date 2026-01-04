package su.afk.kemonos.preferences.useCase

import android.content.SharedPreferences
import androidx.core.content.edit
import su.afk.kemonos.preferences.model.CacheTimeUi
import javax.inject.Inject

/**
 * Время кэширования запрос
 * */
internal class CacheTimestampUseCaseImpl @Inject constructor(
    private val prefs: SharedPreferences
) : ICacheTimestampUseCase {

    override fun getCacheTimestamp(keyPref: String): Long =
        prefs.getLong(keyPref, 0L)

    override fun updateCacheTimestamp(keyPref: String) {
        prefs.edit {
            putLong(keyPref, System.currentTimeMillis())
        }
    }

    override fun clearCacheTimestamp(keyPref: String) {
        prefs.edit {
            remove(keyPref)
        }
    }

    override fun setCacheTimestamp(keyPref: String, value: Long) {
        prefs.edit {
            putLong(keyPref, value)
        }
    }

    override fun cacheTimeUi(key: String, ttlMs: Long): CacheTimeUi {
        val ts = getCacheTimestamp(keyPref = key)
        if (ts == 0L) return CacheTimeUi(lastMs = null, nextMs = null, isFresh = false)

        val now = System.currentTimeMillis()
        val next = ts + ttlMs
        return CacheTimeUi(
            lastMs = ts,
            nextMs = next,
            isFresh = (now - ts) < ttlMs
        )
    }
}