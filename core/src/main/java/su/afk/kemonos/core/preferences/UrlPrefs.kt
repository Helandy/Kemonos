package su.afk.kemonos.core.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import su.afk.kemonos.domain.SelectedSite
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UrlPrefs @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val appScope: CoroutineScope,
) {
    val kemonoUrl: StateFlow<String> = dataStore.data
        .map { it[KEY_K]?.normalizeBaseUrl() ?: DEFAULT_K }
        .stateIn(appScope, SharingStarted.Eagerly, DEFAULT_K)

    val coomerUrl: StateFlow<String> = dataStore.data
        .map { it[KEY_C]?.normalizeBaseUrl() ?: DEFAULT_C }
        .stateIn(appScope, SharingStarted.Eagerly, DEFAULT_C)

    val selectedSite: StateFlow<SelectedSite> = dataStore.data
        .map { it[KEY_SELECTED]?.let(SelectedSite::valueOf) ?: SelectedSite.K }
        .stateIn(appScope, SharingStarted.Eagerly, SelectedSite.K)

    suspend fun setKemonoUrl(url: String) =
        dataStore.edit { it[KEY_K] = url.normalizeBaseUrl() }

    suspend fun setCoomerUrl(url: String) =
        dataStore.edit { it[KEY_C] = url.normalizeBaseUrl() }

    suspend fun setSelectedSite(site: SelectedSite) =
        dataStore.edit { it[KEY_SELECTED] = site.name }

    companion object {
        private val KEY_K = stringPreferencesKey("kemono_url")
        private val KEY_C = stringPreferencesKey("coomer_url")
        private val KEY_SELECTED = stringPreferencesKey("selected_site")
        private const val DEFAULT_K = "https://kemono.cr/api/"
        private const val DEFAULT_C = "https://coomer.st/api/"
    }
}

private fun String.normalizeBaseUrl(): String {
    /** Делаем валидный baseUrl для Retrofit (оканчивается на '/') */
    return buildString {
        append(this@normalizeBaseUrl.trim())
        if (!endsWith("/")) append("/")
    }
}
