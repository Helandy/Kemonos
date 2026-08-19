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
import su.afk.kemonos.domain.SiteCatalog
import su.afk.kemonos.domain.spec
import su.afk.kemonos.utils.url.normalizeBaseUrl
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class UrlPrefs @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @param:Named("AppScope") private val appScope: CoroutineScope,
) {

    /** Базовый адрес API каждого источника. */
    val siteUrls: Map<SelectedSite, StateFlow<String>> =
        SiteCatalog.all.associate { spec ->
            val key = urlKey(spec.site)
            spec.site to dataStore.data
                .map { preferences ->
                    preferences[key]
                        ?.normalizeBaseUrl()
                        ?.takeUnless { it in spec.legacyDefaultApiUrls }
                        ?: spec.defaultApiUrl
                }
                .stateIn(appScope, SharingStarted.Eagerly, spec.defaultApiUrl)
        }

    /** Переопределение хоста превью. Пусто = выводить из базового адреса. */
    val imageHostOverrides: Map<SelectedSite, StateFlow<String>> =
        hostOverrideFlows(::imageHostKey)

    /** Переопределение хоста файлов. Пусто = выводить из базового адреса. */
    val fileHostOverrides: Map<SelectedSite, StateFlow<String>> =
        hostOverrideFlows(::fileHostKey)

    val selectedSite: StateFlow<SelectedSite> = dataStore.data
        .map { it[KEY_SELECTED]?.let(::parseSiteOrNull) ?: SelectedSite.K }
        .stateIn(appScope, SharingStarted.Eagerly, SelectedSite.K)

    init {
        appScope.launch {
            dataStore.edit { preferences ->
                SiteCatalog.all.forEach { spec ->
                    val key = urlKey(spec.site)
                    if (preferences[key]?.normalizeBaseUrl() in spec.legacyDefaultApiUrls) {
                        preferences[key] = spec.defaultApiUrl
                    }
                }
            }
        }
    }

    fun siteUrl(site: SelectedSite): StateFlow<String> = siteUrls.getValue(site)

    fun imageHostOverride(site: SelectedSite): StateFlow<String> =
        imageHostOverrides.getValue(site)

    fun fileHostOverride(site: SelectedSite): StateFlow<String> =
        fileHostOverrides.getValue(site)

    suspend fun setSiteUrl(site: SelectedSite, url: String) =
        dataStore.edit { it[urlKey(site)] = url.normalizeBaseUrl() }

    suspend fun setImageHostOverride(site: SelectedSite, url: String) =
        dataStore.edit { it[imageHostKey(site)] = url.normalizeHostOverride() }

    suspend fun setFileHostOverride(site: SelectedSite, url: String) =
        dataStore.edit { it[fileHostKey(site)] = url.normalizeHostOverride() }

    suspend fun setSelectedSite(site: SelectedSite) =
        dataStore.edit { it[KEY_SELECTED] = site.name }

    private fun hostOverrideFlows(
        key: (SelectedSite) -> Preferences.Key<String>,
    ): Map<SelectedSite, StateFlow<String>> =
        SiteCatalog.all.associate { spec ->
            val prefKey = key(spec.site)
            spec.site to dataStore.data
                .map { it[prefKey].orEmpty() }
                .stateIn(appScope, SharingStarted.Eagerly, "")
        }

    private companion object {
        val KEY_SELECTED = stringPreferencesKey("selected_site")

        /** Ключи строятся из стабильного слага — совпадают с историческими именами. */
        fun urlKey(site: SelectedSite) =
            stringPreferencesKey("${site.spec.slug}_url")

        fun imageHostKey(site: SelectedSite) =
            stringPreferencesKey("${site.spec.slug}_image_host_override")

        fun fileHostKey(site: SelectedSite) =
            stringPreferencesKey("${site.spec.slug}_file_host_override")

        /** Неизвестное значение (например, источник из более новой версии) не должно ронять приложение. */
        fun parseSiteOrNull(raw: String): SelectedSite? =
            SelectedSite.entries.firstOrNull { it.name == raw }

        fun String.normalizeHostOverride(): String = trim().trimEnd('/')
    }
}
