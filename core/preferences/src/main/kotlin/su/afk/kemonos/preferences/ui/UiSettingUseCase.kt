package su.afk.kemonos.preferences.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import su.afk.kemonos.preferences.ui.UiSettingModel.Companion.DEFAULT_CREATORS_VIEW_MODE
import javax.inject.Inject

internal class UiSettingUseCase @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : IUiSettingUseCase {

    override val prefs: Flow<UiSettingModel> = dataStore.data.map { p ->
        UiSettingModel(
            creatorsViewMode = p.readEnum(CREATORS_VIEW_MODE, DEFAULT_CREATORS_VIEW_MODE),

            skipApiCheckOnLogin = p[SKIP_API_CHECK_ON_LOGIN] ?: false,

//            downloadPathTemplate = p.readEnum(KEY_DOWNLOAD_PATH_TEMPLATE, DownloadPathTemplate.USER_POST_DATE_ID),
//
//            suggestRandomCreatorsOnLaunch = p[KEY_SUGGEST_RANDOM_CREATORS] ?: false,
//
//            popularDateFormat = p.readEnum(KEY_POPULAR_DATE_FORMAT, PopularDateFormat.MONTH_MONTH),
//
//            translateTarget = p.readEnum(KEY_TRANSLATE_TARGET, TranslationTarget.APP),
//            translateLanguage = p[KEY_TRANSLATE_LANGUAGE] ?: "en",
//
//            experimentalCalendar = p[KEY_EXPERIMENTAL_CALENDAR] ?: false,
        )
    }

    /** Вид отображения авторов  */
    override suspend fun setCreatorsViewMode(value: CreatorViewMode) {
        dataStore.edit {
            it[CREATORS_VIEW_MODE] = value.name
        }
    }

    /** Debug: пропустить проверку API при входе */
    override suspend fun setSkipApiCheckOnLogin(value: Boolean) {
        dataStore.edit {
            it[SKIP_API_CHECK_ON_LOGIN] = value
        }
    }


    // ---- helpers ----
    private inline fun <reified T : Enum<T>> Preferences.readEnum(
        key: Preferences.Key<String>,
        default: T
    ): T {
        val raw = this[key] ?: return default
        return runCatching { enumValueOf<T>(raw) }.getOrDefault(default)
    }

    private companion object {
        val CREATORS_VIEW_MODE = stringPreferencesKey("CREATORS_VIEW_MODE")
        val SKIP_API_CHECK_ON_LOGIN = booleanPreferencesKey("SKIP_API_CHECK_ON_LOGIN")

//        val KEY_DOWNLOAD_CATALOG_VERSION = intPreferencesKey("ui_download_catalog_version")
//        val KEY_DOWNLOAD_PATH_TEMPLATE = stringPreferencesKey("ui_download_path_template")
//        val KEY_SUGGEST_RANDOM_CREATORS = booleanPreferencesKey("ui_suggest_random_creators")
    }
}