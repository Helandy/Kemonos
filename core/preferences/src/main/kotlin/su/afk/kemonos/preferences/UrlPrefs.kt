package su.afk.kemonos.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.utils.url.normalizeBaseUrl
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class UrlPrefs @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @param:Named("AppScope") private val appScope: CoroutineScope,
) {
    val kemonoUrl: StateFlow<String> = dataStore.data
        .map { it[KEY_K]?.normalizeBaseUrl() ?: DEFAULT_K }
        .stateIn(appScope, SharingStarted.Eagerly, DEFAULT_K)

    val coomerUrl: StateFlow<String> = dataStore.data
        .map { it[KEY_C]?.normalizeBaseUrl() ?: DEFAULT_C }
        .stateIn(appScope, SharingStarted.Eagerly, DEFAULT_C)

    val pawchiveUrl: StateFlow<String> = dataStore.data
        .map { preferences ->
            preferences[KEY_P]
                ?.normalizeBaseUrl()
                ?.takeUnless { it == LEGACY_DEFAULT_P }
                ?: DEFAULT_P
        }
        .stateIn(appScope, SharingStarted.Eagerly, DEFAULT_P)

    val pawchiveImageHostOverride: StateFlow<String> = dataStore.data
        .map { it[KEY_P_IMAGE_HOST].orEmpty() }
        .stateIn(appScope, SharingStarted.Eagerly, "")

    val pawchiveFileHostOverride: StateFlow<String> = dataStore.data
        .map { it[KEY_P_FILE_HOST].orEmpty() }
        .stateIn(appScope, SharingStarted.Eagerly, "")

    val selectedSite: StateFlow<SelectedSite> = dataStore.data
        .map { it[KEY_SELECTED]?.let(SelectedSite::valueOf) ?: SelectedSite.K }
        .stateIn(appScope, SharingStarted.Eagerly, SelectedSite.K)

    init {
        appScope.launch {
            dataStore.edit { preferences ->
                if (preferences[KEY_P]?.normalizeBaseUrl() == LEGACY_DEFAULT_P) {
                    preferences[KEY_P] = DEFAULT_P
                }
            }
        }
    }

    suspend fun setKemonoUrl(url: String) =
        dataStore.edit { it[KEY_K] = url.normalizeBaseUrl() }

    suspend fun setCoomerUrl(url: String) =
        dataStore.edit { it[KEY_C] = url.normalizeBaseUrl() }

    suspend fun setPawchiveUrl(url: String) =
        dataStore.edit { it[KEY_P] = url.normalizeBaseUrl() }

    suspend fun setPawchiveImageHostOverride(url: String) =
        dataStore.edit { it[KEY_P_IMAGE_HOST] = url.trim().trimEnd('/') }

    suspend fun setPawchiveFileHostOverride(url: String) =
        dataStore.edit { it[KEY_P_FILE_HOST] = url.trim().trimEnd('/') }

    suspend fun setSelectedSite(site: SelectedSite) =
        dataStore.edit { it[KEY_SELECTED] = site.name }

    companion object {
        private val KEY_K = stringPreferencesKey("kemono_url")
        private val KEY_C = stringPreferencesKey("coomer_url")
        private val KEY_P = stringPreferencesKey("pawchive_url")
        private val KEY_P_IMAGE_HOST = stringPreferencesKey("pawchive_image_host_override")
        private val KEY_P_FILE_HOST = stringPreferencesKey("pawchive_file_host_override")
        private val KEY_SELECTED = stringPreferencesKey("selected_site")
        private const val DEFAULT_K = "https://kemono.cr/api/"
        private const val DEFAULT_C = "https://coomer.st/api/"
        private const val DEFAULT_P = "https://pawchive.pw/api/"
        private const val LEGACY_DEFAULT_P = "https://pawchive.st/api/"
    }
}
