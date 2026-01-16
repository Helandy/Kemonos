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
    }
}