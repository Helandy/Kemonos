package su.afk.kemonos.core.domain.useCase

import android.content.SharedPreferences
import androidx.core.content.edit
import su.afk.kemonos.core.api.domain.useCase.CacheTimestampUseCase
import javax.inject.Inject

/**
 * Время кэширования запрос
 * */
internal class CacheTimestampUseCaseImpl @Inject constructor(
    private val prefs: SharedPreferences
) : CacheTimestampUseCase {

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
}