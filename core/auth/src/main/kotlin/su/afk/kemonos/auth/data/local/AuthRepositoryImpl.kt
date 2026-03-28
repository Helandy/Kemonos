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

    private object DsKeys {
        val K_USER_JSON = stringPreferencesKey("k_user_json")
        val C_USER_JSON = stringPreferencesKey("c_user_json")
    }

    private object SpKeys {
        const val K_SESSION = "k_session"
        const val C_SESSION = "c_session"
    }

    private val refresh = MutableStateFlow(0)

    override val authState: Flow<AuthState> =
        combine(
            dataStore.data,
            refresh,
        ) { prefs, _ ->
            fun readUser(userKey: Preferences.Key<String>): AuthUser? {
                val userJson = prefs[userKey] ?: return null
                return runCatching { json.decodeFromString<AuthUser>(userJson) }.getOrNull()
            }

            val kUser = readUser(DsKeys.K_USER_JSON)
            val cUser = readUser(DsKeys.C_USER_JSON)

            AuthState(
                kemono = SiteAuthState(
                    session = readSession(SelectedSite.K),
                    user = kUser,
                ),
                coomer = SiteAuthState(
                    session = readSession(SelectedSite.C),
                    user = cUser,
                ),
            )
        }

    override suspend fun saveAuth(site: SelectedSite, session: String, user: AuthUser) {
        securePrefs.edit(commit = true) {
            when (site) {
                SelectedSite.K -> putString(SpKeys.K_SESSION, session)
                SelectedSite.C -> putString(SpKeys.C_SESSION, session)
            }
        }

        val userJson = json.encodeToString(user)
        dataStore.edit { prefs ->
            when (site) {
                SelectedSite.K -> prefs[DsKeys.K_USER_JSON] = userJson
                SelectedSite.C -> prefs[DsKeys.C_USER_JSON] = userJson
            }
        }

        refresh.value++
    }

    override suspend fun clearAuth(site: SelectedSite) {
        securePrefs.edit(commit = true) {
            when (site) {
                SelectedSite.K -> remove(SpKeys.K_SESSION)
                SelectedSite.C -> remove(SpKeys.C_SESSION)
            }
        }

        dataStore.edit { prefs ->
            when (site) {
                SelectedSite.K -> prefs.remove(DsKeys.K_USER_JSON)
                SelectedSite.C -> prefs.remove(DsKeys.C_USER_JSON)
            }
        }

        refresh.value++
    }

    override suspend fun clearAll() {
        securePrefs.edit(commit = true) { clear() }
        dataStore.edit { it.clear() }
        refresh.value++
    }

    override suspend fun getSession(site: SelectedSite): String? = readSession(site)

    private fun readSession(site: SelectedSite): String? =
        when (site) {
            SelectedSite.K -> securePrefs.getString(SpKeys.K_SESSION, null)
            SelectedSite.C -> securePrefs.getString(SpKeys.C_SESSION, null)
        }
}
