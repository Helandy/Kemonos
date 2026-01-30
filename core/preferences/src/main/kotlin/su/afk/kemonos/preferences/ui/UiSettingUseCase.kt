package su.afk.kemonos.preferences.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
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

            dateFormatMode = p.readEnum(DATE_FORMAT_MODE, UiSettingModel.DEFAULT_DATE_FORMAT_MODE),

            postsSize = p.readEnum(POSTS_SIZE, UiSettingModel.DEFAULT_POSTS_SIZE),

            coilCacheSizeMb = p[COIL_CACHE_SIZE_MB] ?: UiSettingModel.DEFAULT_COIL_CACHE_SIZE,
            previewVideoSizeMb = p[PREVIEW_VIDEO_SIZE_MB] ?: UiSettingModel.DEFAULT_VIDEO_PREVIEW_SIZE,

            showPreviewVideo = p[SHOW_PREVIEW_VIDEO] ?: UiSettingModel.DEFAULT_SHOW_VIDEO_PREVIEW,
            blurImages = p[BLUR_IMAGES] ?: UiSettingModel.DEFAULT_BLUR_PICTURE,
            experimentalCalendar = p[EXPERIMENTAL_CALENDAR] ?: UiSettingModel.DEFAULT_EXPERIMENTAL_CALENDAR,

            downloadFolderMode = p.readEnum(DOWNLOAD_FOLDER_MODE, UiSettingModel.DEFAULT_DOWNLOAD_FOLDER_MODE),
            addServiceName = p[ADD_SERVICE_NAME] ?: UiSettingModel.DEFAULT_ADD_SERVICE_NAME,
            useExternalMetaData = p[USE_EXTERNAL_METADATA] ?: UiSettingModel.USE_EXTERNAL_METADATA
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

    /** Формат даты в приложении */
    override suspend fun setDateFormatMode(value: DateFormatMode) {
        dataStore.edit { it[DATE_FORMAT_MODE] = value.name }
    }

    /** Размер постов в сетке */
    override suspend fun setPostsSize(value: PostsSize) {
        dataStore.edit { it[POSTS_SIZE] = value.name }
    }

    /** Размер кэша картинок (MB) */
    override suspend fun setCoilCacheSizeMb(value: Int) {
        dataStore.edit { it[COIL_CACHE_SIZE_MB] = value.coerceAtLeast(0) }
    }

    /** Размер кэша превьюшек (MB) */
    override suspend fun setPreviewVideoSizeMb(value: Int) {
        dataStore.edit { it[PREVIEW_VIDEO_SIZE_MB] = value.coerceAtLeast(0) }
    }

    /** Показывать ли превью видео */
    override suspend fun setShowPreviewVideo(value: Boolean) {
        dataStore.edit { it[SHOW_PREVIEW_VIDEO] = value }
    }

    /** Блюрить все картинки */
    override suspend fun setBlurImages(value: Boolean) {
        dataStore.edit { it[BLUR_IMAGES] = value }
    }

    /** Экспериментальный календарь */
    override suspend fun setExperimentalCalendar(value: Boolean) {
        dataStore.edit { it[EXPERIMENTAL_CALENDAR] = value }
    }

    /** Вид папок для скачивания */
    override suspend fun setDownloadFolderMode(value: DownloadFolderMode) {
        dataStore.edit { it[DOWNLOAD_FOLDER_MODE] = value.name }
    }

    /** Добавлять префикс сервиса при скачивании */
    override suspend fun setAddServiceName(value: Boolean) {
        dataStore.edit { it[ADD_SERVICE_NAME] = value }
    }

    /** Использовать внешнее хранилище метадатнных */
    override suspend fun setUseExternalMetaData(value: Boolean) {
        dataStore.edit { it[USE_EXTERNAL_METADATA] = value }
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

        val DATE_FORMAT_MODE = stringPreferencesKey("DATE_FORMAT_MODE")

        val POSTS_SIZE = stringPreferencesKey("POSTS_SIZE")

        val COIL_CACHE_SIZE_MB = intPreferencesKey("COIL_CACHE_SIZE_MB")
        val PREVIEW_VIDEO_SIZE_MB = intPreferencesKey("PREVIEW_VIDEO_SIZE_MB")

        val SHOW_PREVIEW_VIDEO = booleanPreferencesKey("SHOW_PREVIEW_VIDEO")
        val BLUR_IMAGES = booleanPreferencesKey("BLUR_IMAGES")
        val EXPERIMENTAL_CALENDAR = booleanPreferencesKey("EXPERIMENTAL_CALENDAR")

        val DOWNLOAD_FOLDER_MODE = stringPreferencesKey("DOWNLOAD_FOLDER_MODE")
        val ADD_SERVICE_NAME = booleanPreferencesKey("ADD_SERVICE_NAME")
        val USE_EXTERNAL_METADATA = booleanPreferencesKey("USE_EXTERNAL_METADATA")
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
