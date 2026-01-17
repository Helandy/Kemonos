package su.afk.kemonos.preferences.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UiSettingUseCase @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : IUiSettingUseCase {

    override val prefs: Flow<UiSettingModel> = dataStore.data.map { p ->
        UiSettingModel(
            skipApiCheckOnLogin = p[SKIP_API_CHECK_ON_LOGIN] ?: false,
            creatorsViewMode = p.readEnum(CREATORS_VIEW_MODE, UiSettingModel.DEFAULT_CREATORS_VIEW_MODE),
            creatorsFavoriteViewMode = p.readEnum(
                CREATORS_FAVORITE_VIEW_MODE,
                UiSettingModel.DEFAULT_CREATORS_VIEW_MODE
            ),

            profilePostsViewMode = p.readEnum(PROFILE_POSTS_VIEW_MODE, UiSettingModel.DEFAULT_POSTS_VIEW_MODE),
            favoritePostsViewMode = p.readEnum(FAVORITE_POSTS_VIEW_MODE, UiSettingModel.DEFAULT_POSTS_VIEW_MODE),
            popularPostsViewMode = p.readEnum(POPULAR_POSTS_VIEW_MODE, UiSettingModel.DEFAULT_POSTS_VIEW_MODE),
            tagsPostsViewMode = p.readEnum(TAGS_POSTS_VIEW_MODE, UiSettingModel.DEFAULT_POSTS_VIEW_MODE),
            searchPostsViewMode = p.readEnum(SEARCH_POSTS_VIEW_MODE, UiSettingModel.DEFAULT_POSTS_VIEW_MODE),

            suggestRandomAuthors = p[SUGGEST_RANDOM_AUTHORS] ?: UiSettingModel.DEFAULT_SUGGEST_RANDOM_AUTHORS,
            translateTarget = p.readEnum(TRANSLATE_TARGET, UiSettingModel.DEFAULT_TRANSLATE_TARGET),
            randomButtonPlacement = p.readEnum(RANDOM_BUTTON_PLACEMENT, UiSettingModel.DEFAULT_RANDOM_BUTTON_PLACEMENT),
            translateLanguageTag = p[TRANSLATE_LANGUAGE_TAG] ?: UiSettingModel.DEFAULT_TRANSLATE_LANGUAGE_TAG,
        )
    }

    /** Debug: пропустить проверку API при входе */
    override suspend fun setSkipApiCheckOnLogin(value: Boolean) {
        dataStore.edit {
            it[SKIP_API_CHECK_ON_LOGIN] = value
        }
    }

    /** Вид отображения авторов  */
    override suspend fun setCreatorsViewMode(value: CreatorViewMode) {
        dataStore.edit {
            it[CREATORS_VIEW_MODE] = value.name
        }
    }

    /** Вид отображения авторов избранное */
    override suspend fun setCreatorsFavoriteViewMode(value: CreatorViewMode) {
        dataStore.edit {
            it[CREATORS_FAVORITE_VIEW_MODE] = value.name
        }
    }

    /** Вид отображения постов */
    override suspend fun setProfilePostsViewMode(value: PostsViewMode) {
        dataStore.edit { it[PROFILE_POSTS_VIEW_MODE] = value.name }
    }

    override suspend fun setFavoritePostsViewMode(value: PostsViewMode) {
        dataStore.edit { it[FAVORITE_POSTS_VIEW_MODE] = value.name }
    }

    override suspend fun setPopularPostsViewMode(value: PostsViewMode) {
        dataStore.edit { it[POPULAR_POSTS_VIEW_MODE] = value.name }
    }

    override suspend fun setTagsPostsViewMode(value: PostsViewMode) {
        dataStore.edit { it[TAGS_POSTS_VIEW_MODE] = value.name }
    }

    override suspend fun setSearchPostsViewMode(value: PostsViewMode) {
        dataStore.edit { it[SEARCH_POSTS_VIEW_MODE] = value.name }
    }

    /** Предлагать рандомных авторов */
    override suspend fun setSuggestRandomAuthors(value: Boolean) {
        dataStore.edit {
            it[SUGGEST_RANDOM_AUTHORS] = value
        }
    }

    /** Способ перевода */
    override suspend fun setTranslateTarget(value: TranslateTarget) {
        dataStore.edit { it[TRANSLATE_TARGET] = value.name }
    }

    /** Где показывать кнопку "рандом" */
    override suspend fun setRandomButtonPlacement(value: RandomButtonPlacement) {
        dataStore.edit { it[RANDOM_BUTTON_PLACEMENT] = value.name }
    }

    /** Язык, на который переводим */
    override suspend fun setTranslateLanguageTag(value: String) {
        dataStore.edit { it[TRANSLATE_LANGUAGE_TAG] = value }
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
        val SKIP_API_CHECK_ON_LOGIN = booleanPreferencesKey("SKIP_API_CHECK_ON_LOGIN")
        val CREATORS_VIEW_MODE = stringPreferencesKey("CREATORS_VIEW_MODE")
        val CREATORS_FAVORITE_VIEW_MODE = stringPreferencesKey("CREATORS_FAVORITE_VIEW_MODE")

        val PROFILE_POSTS_VIEW_MODE = stringPreferencesKey("PROFILE_POSTS_VIEW_MODE")
        val FAVORITE_POSTS_VIEW_MODE = stringPreferencesKey("FAVORITE_POSTS_VIEW_MODE")
        val POPULAR_POSTS_VIEW_MODE = stringPreferencesKey("POPULAR_POSTS_VIEW_MODE")
        val TAGS_POSTS_VIEW_MODE = stringPreferencesKey("TAGS_POSTS_VIEW_MODE")
        val SEARCH_POSTS_VIEW_MODE = stringPreferencesKey("SEARCH_POSTS_VIEW_MODE")

        val SUGGEST_RANDOM_AUTHORS = booleanPreferencesKey("SUGGEST_RANDOM_AUTHORS")
        val TRANSLATE_TARGET = stringPreferencesKey("TRANSLATE_TARGET")
        val RANDOM_BUTTON_PLACEMENT = stringPreferencesKey("RANDOM_BUTTON_PLACEMENT")
        val TRANSLATE_LANGUAGE_TAG = stringPreferencesKey("TRANSLATE_LANGUAGE")
    }
}