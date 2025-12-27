package su.afk.kemonos.core.auth

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
import su.afk.kemonos.core.di.AuthDataStore
import su.afk.kemonos.core.di.AuthEncryptedPrefs
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.api.model.Login
import javax.inject.Inject
import javax.inject.Singleton

interface IAuthLocalDataSource {
    /** Текущее состояние авторизации (null, если не залогинен) */
    val authState: Flow<AuthState>

    /** Сохранить авторизацию для конкретного сайта */
    suspend fun saveAuth(
        site: SelectedSite,
        session: String,
        user: Login,
    )

    /** Очистить авторизацию только для конкретного сайта */
    suspend fun clearAuth(site: SelectedSite)

    /** Полный logout сразу везде */
    suspend fun clearAll()
}


@Singleton
internal class AuthLocalDataSource @Inject constructor(
    /**
     * DataStore (НЕ шифрованный): храним тут "не-секретное" — например user (Login) в JSON.
     * DataStore хорош тем, что реактивный (Flow) и удобен для state.
     */
    @AuthDataStore private val dataStore: DataStore<Preferences>,

    /**
     * EncryptedSharedPreferences (ШИФРОВАННЫЙ): храним тут секрет — session/token/cookie.
     * Это важно, потому что обычный DataStore<Preferences> не шифрует значения.
     */
    @AuthEncryptedPrefs private val securePrefs: SharedPreferences,

    /**
     * Json для сериализации/десериализации Login.
     */
    private val json: Json,
) : IAuthLocalDataSource {

    /**
     * Ключи DataStore — только user в JSON (без session).
     */
    private object DsKeys {
        val K_USER_JSON = stringPreferencesKey("k_user_json")
        val C_USER_JSON = stringPreferencesKey("c_user_json")
    }

    /**
     * Ключи EncryptedSharedPreferences — только session.
     */
    private object SpKeys {
        const val K_SESSION = "k_session"
        const val C_SESSION = "c_session"
    }

    /**
     * SharedPreferences не даёт Flow сам по себе.
     * Поэтому делаем "триггер" — после save/clear увеличиваем счётчик,
     * и authState пересчитывается.
     *
     * Альтернатива: callbackFlow + OnSharedPreferenceChangeListener.
     * Но для простого кейса счётчик — норм.
     */
    private val refresh = MutableStateFlow(0)

    override val authState: Flow<AuthState> =
        combine(
            dataStore.data,
            refresh,
        ) { prefs, _ ->

            /** user из DataStore */
            fun readUser(userKey: Preferences.Key<String>): Login? {
                val userJson = prefs[userKey] ?: return null
                return runCatching { json.decodeFromString<Login>(userJson) }.getOrNull()
            }

            /** session из EncryptedSharedPreferences */
            fun readSession(site: SelectedSite): String? =
                when (site) {
                    SelectedSite.K -> securePrefs.getString(SpKeys.K_SESSION, null)
                    SelectedSite.C -> securePrefs.getString(SpKeys.C_SESSION, null)
                }

            val kUser = readUser(DsKeys.K_USER_JSON)
            val cUser = readUser(DsKeys.C_USER_JSON)

            /** бщее состояние */
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

    override suspend fun saveAuth(site: SelectedSite, session: String, user: Login) {
        /**
         * 1) Секрет сохраняем в EncryptedSharedPreferences
         * commit=true — чтобы синхронно записать (чуть медленнее, но надёжнее).
         */
        securePrefs.edit(commit = true) {
            when (site) {
                SelectedSite.K -> putString(SpKeys.K_SESSION, session)
                SelectedSite.C -> putString(SpKeys.C_SESSION, session)
            }
        }

        /**
         * 2) User сохраняем в DataStore (как JSON строку)
         */
        val userJson = json.encodeToString(user)
        dataStore.edit { prefs ->
            when (site) {
                SelectedSite.K -> prefs[DsKeys.K_USER_JSON] = userJson
                SelectedSite.C -> prefs[DsKeys.C_USER_JSON] = userJson
            }
        }

        /**
         * 3) Триггерим refresh, чтобы authState пересчитался и сессия обновилась в Flow
         */
        refresh.value++
    }

    override suspend fun clearAuth(site: SelectedSite) {
        /**
         * 1) Удаляем session из EncryptedSharedPreferences
         */
        securePrefs.edit(commit = true) {
            when (site) {
                SelectedSite.K -> remove(SpKeys.K_SESSION)
                SelectedSite.C -> remove(SpKeys.C_SESSION)
            }
        }

        /**
         * 2) Удаляем user из DataStore
         */
        dataStore.edit { prefs ->
            when (site) {
                SelectedSite.K -> prefs.remove(DsKeys.K_USER_JSON)
                SelectedSite.C -> prefs.remove(DsKeys.C_USER_JSON)
            }
        }

        /**
         * 3) Триггерим refresh
         */
        refresh.value++
    }

    override suspend fun clearAll() {
        /**
         * Полный logout:
         * - чистим и session в encrypted prefs,
         * - чистим и user в datastore,
         * - триггерим refresh.
         */
        securePrefs.edit(commit = true) { clear() }
        dataStore.edit { it.clear() }
        refresh.value++
    }
}