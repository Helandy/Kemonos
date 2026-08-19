package su.afk.kemonos.auth.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.serialization.json.Json
import su.afk.kemonos.auth.di.AuthDataStore
import su.afk.kemonos.auth.di.AuthEncryptedPrefs
import su.afk.kemonos.auth.domain.model.AuthState
import su.afk.kemonos.auth.domain.model.SiteAuthState
import su.afk.kemonos.auth.domain.repository.AuthRepository
import su.afk.kemonos.auth.domain.repository.AuthSessionProvider
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.AuthUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AuthRepositoryImpl @Inject constructor(
    @param:AuthDataStore private val dataStore: DataStore<Preferences>,
    @param:AuthEncryptedPrefs private val securePrefs: SharedPreferences,
    private val json: Json,
) : AuthRepository, AuthSessionProvider {

    private companion object Keys {
        /** Историческое именование: SelectedSite.K -> "k_user_json". */
        fun userJson(site: SelectedSite) =
            stringPreferencesKey("${site.name.lowercase()}_user_json")

        fun session(site: SelectedSite) = "${site.name.lowercase()}_session"
    }

    private val refresh = MutableStateFlow(0)

    override val authState: Flow<AuthState> =
        combine(
            dataStore.data,
            refresh,
        ) { prefs, _ ->
            fun readUser(site: SelectedSite): AuthUser? {
                val userJson = prefs[Keys.userJson(site)] ?: return null
                return runCatching { json.decodeFromString<AuthUser>(userJson) }.getOrNull()
            }

            AuthState(
                sites = SelectedSite.entries.associateWith { site ->
                    SiteAuthState(
                        session = readSession(site),
                        user = readUser(site),
                    )
                },
            )
        }

    override suspend fun saveAuth(site: SelectedSite, session: String, user: AuthUser) {
        securePrefs.edit(commit = true) { putString(Keys.session(site), session) }

        val userJson = json.encodeToString(user)
        dataStore.edit { prefs -> prefs[Keys.userJson(site)] = userJson }

        refresh.value++
    }

    override suspend fun clearAuth(site: SelectedSite) {
        securePrefs.edit(commit = true) { remove(Keys.session(site)) }

        dataStore.edit { prefs -> prefs.remove(Keys.userJson(site)) }

        refresh.value++
    }

    override suspend fun clearAll() {
        securePrefs.edit(commit = true) { clear() }
        dataStore.edit { it.clear() }
        refresh.value++
    }

    override suspend fun getSession(site: SelectedSite): String? = readSession(site)

    private fun readSession(site: SelectedSite): String? =
        securePrefs.getString(Keys.session(site), null)
}
