package su.afk.kemonos.preferences.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import su.afk.kemonos.preferences.ui.UiSettingKey.ADD_SERVICE_NAME
import su.afk.kemonos.preferences.ui.UiSettingKey.APP_THEME_MODE
import su.afk.kemonos.preferences.ui.UiSettingKey.AUTOPLAY_COMMUNITY_VIDEO
import su.afk.kemonos.preferences.ui.UiSettingKey.BLUR_IMAGES
import su.afk.kemonos.preferences.ui.UiSettingKey.COIL_CACHE_SIZE_MB
import su.afk.kemonos.preferences.ui.UiSettingKey.CREATORS_FAVORITE_VIEW_MODE
import su.afk.kemonos.preferences.ui.UiSettingKey.CREATORS_GITHUB_RATE_BANNER_DISABLED
import su.afk.kemonos.preferences.ui.UiSettingKey.CREATORS_GITHUB_RATE_BANNER_INSTALL_TS_MS
import su.afk.kemonos.preferences.ui.UiSettingKey.CREATORS_VIEW_MODE
import su.afk.kemonos.preferences.ui.UiSettingKey.CREATOR_PROFILE_HIDDEN_TABS
import su.afk.kemonos.preferences.ui.UiSettingKey.CREATOR_PROFILE_TABS_ORDER
import su.afk.kemonos.preferences.ui.UiSettingKey.CROP_POST_PREVIEW_VIDEO
import su.afk.kemonos.preferences.ui.UiSettingKey.CROP_VIDEO_PREVIEW
import su.afk.kemonos.preferences.ui.UiSettingKey.DATE_FORMAT_MODE
import su.afk.kemonos.preferences.ui.UiSettingKey.DISCORD_COMMUNITY_REVERSE_ORDER_DEFAULT
import su.afk.kemonos.preferences.ui.UiSettingKey.DOWNLOAD_FOLDER_MODE
import su.afk.kemonos.preferences.ui.UiSettingKey.EXPERIMENTAL_CALENDAR
import su.afk.kemonos.preferences.ui.UiSettingKey.FAVORITE_POSTS_GRID_SIZE
import su.afk.kemonos.preferences.ui.UiSettingKey.FAVORITE_POSTS_VIEW_MODE
import su.afk.kemonos.preferences.ui.UiSettingKey.POPULAR_POSTS_GRID_SIZE
import su.afk.kemonos.preferences.ui.UiSettingKey.POPULAR_POSTS_VIEW_MODE
import su.afk.kemonos.preferences.ui.UiSettingKey.POSTS_SIZE
import su.afk.kemonos.preferences.ui.UiSettingKey.PROFILE_POSTS_GRID_SIZE
import su.afk.kemonos.preferences.ui.UiSettingKey.PROFILE_POSTS_VIEW_MODE
import su.afk.kemonos.preferences.ui.UiSettingKey.RANDOM_BUTTON_PLACEMENT
import su.afk.kemonos.preferences.ui.UiSettingKey.SEARCH_POSTS_GRID_SIZE
import su.afk.kemonos.preferences.ui.UiSettingKey.SEARCH_POSTS_VIEW_MODE
import su.afk.kemonos.preferences.ui.UiSettingKey.SHOW_COMMENTS_IN_POST
import su.afk.kemonos.preferences.ui.UiSettingKey.SHOW_IMAGE_PREVIEW_ACTION
import su.afk.kemonos.preferences.ui.UiSettingKey.SHOW_IMAGE_PREVIEW_DOWNLOAD_ACTION
import su.afk.kemonos.preferences.ui.UiSettingKey.SHOW_IMAGE_PREVIEW_SHARE_ACTION
import su.afk.kemonos.preferences.ui.UiSettingKey.SHOW_PREVIEW_VIDEO
import su.afk.kemonos.preferences.ui.UiSettingKey.SITE_DISPLAY_MODE
import su.afk.kemonos.preferences.ui.UiSettingKey.SKIP_API_CHECK_ON_LOGIN
import su.afk.kemonos.preferences.ui.UiSettingKey.SUGGEST_RANDOM_AUTHORS
import su.afk.kemonos.preferences.ui.UiSettingKey.TAGS_POSTS_GRID_SIZE
import su.afk.kemonos.preferences.ui.UiSettingKey.TAGS_POSTS_VIEW_MODE
import su.afk.kemonos.preferences.ui.UiSettingKey.TRANSLATE_LANGUAGE_TAG
import su.afk.kemonos.preferences.ui.UiSettingKey.TRANSLATE_TARGET
import su.afk.kemonos.preferences.ui.UiSettingKey.USE_EXTERNAL_METADATA
import su.afk.kemonos.preferences.ui.UiSettingKey.VIDEO_PREVIEW_ASPECT_RATIO
import su.afk.kemonos.preferences.ui.UiSettingKey.VIDEO_PREVIEW_SERVER_URL
import javax.inject.Inject

internal class UiSettingUseCase @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : IUiSettingUseCase {

    override val prefs: Flow<UiSettingModel> = dataStore.data.map { p ->
        val legacyPostsSize = p.readEnum(POSTS_SIZE, UiSettingModel.DEFAULT_POSTS_SIZE)
        val siteDisplayMode = p.readEnum(SITE_DISPLAY_MODE, UiSettingModel.DEFAULT_SITE_DISPLAY_MODE)

        UiSettingModel(
            skipApiCheckOnLogin = p[SKIP_API_CHECK_ON_LOGIN] ?: false,
            siteDisplayMode = siteDisplayMode,
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
            creatorProfileTabsOrder = p.readTabOrder(CREATOR_PROFILE_TABS_ORDER),
            creatorProfileHiddenTabs = p.readHiddenTabs(CREATOR_PROFILE_HIDDEN_TABS),

            suggestRandomAuthors = p[SUGGEST_RANDOM_AUTHORS] ?: UiSettingModel.DEFAULT_SUGGEST_RANDOM_AUTHORS,
            translateTarget = p.readEnum(TRANSLATE_TARGET, UiSettingModel.DEFAULT_TRANSLATE_TARGET),
            randomButtonPlacement = p.readEnum(RANDOM_BUTTON_PLACEMENT, UiSettingModel.DEFAULT_RANDOM_BUTTON_PLACEMENT),
            translateLanguageTag = p[TRANSLATE_LANGUAGE_TAG] ?: UiSettingModel.DEFAULT_TRANSLATE_LANGUAGE_TAG,
            appThemeMode = p.readEnum(APP_THEME_MODE, UiSettingModel.DEFAULT_APP_THEME_MODE),

            dateFormatMode = p.readEnum(DATE_FORMAT_MODE, UiSettingModel.DEFAULT_DATE_FORMAT_MODE),

            postsSize = legacyPostsSize,
            profilePostsGridSize = p.readEnum(PROFILE_POSTS_GRID_SIZE, legacyPostsSize),
            favoritePostsGridSize = p.readEnum(FAVORITE_POSTS_GRID_SIZE, legacyPostsSize),
            popularPostsGridSize = p.readEnum(POPULAR_POSTS_GRID_SIZE, legacyPostsSize),
            tagsPostsGridSize = p.readEnum(TAGS_POSTS_GRID_SIZE, legacyPostsSize),
            searchPostsGridSize = p.readEnum(SEARCH_POSTS_GRID_SIZE, legacyPostsSize),

            coilCacheSizeMb = p[COIL_CACHE_SIZE_MB] ?: UiSettingModel.DEFAULT_COIL_CACHE_SIZE,

            showPreviewVideo = p[SHOW_PREVIEW_VIDEO] ?: UiSettingModel.DEFAULT_SHOW_VIDEO_PREVIEW,
            autoplayCommunityVideo = p[AUTOPLAY_COMMUNITY_VIDEO] ?: UiSettingModel.DEFAULT_AUTOPLAY_COMMUNITY_VIDEO,
            discordCommunityReverseOrderDefault = p[DISCORD_COMMUNITY_REVERSE_ORDER_DEFAULT]
                ?: UiSettingModel.DEFAULT_DISCORD_COMMUNITY_REVERSE_ORDER_DEFAULT,
            blurImages = p[BLUR_IMAGES] ?: UiSettingModel.DEFAULT_BLUR_PICTURE,
            showImagePreviewDownloadAction = p[SHOW_IMAGE_PREVIEW_DOWNLOAD_ACTION]
                ?: p[SHOW_IMAGE_PREVIEW_ACTION]
                ?: UiSettingModel.DEFAULT_SHOW_IMAGE_PREVIEW_DOWNLOAD_ACTION,
            showImagePreviewShareAction = p[SHOW_IMAGE_PREVIEW_SHARE_ACTION]
                ?: p[SHOW_IMAGE_PREVIEW_ACTION]
                ?: UiSettingModel.DEFAULT_SHOW_IMAGE_PREVIEW_SHARE_ACTION,
            showCommentsInPost = p[SHOW_COMMENTS_IN_POST] ?: UiSettingModel.DEFAULT_SHOW_COMMENTS_IN_POST,
            experimentalCalendar = p[EXPERIMENTAL_CALENDAR] ?: UiSettingModel.DEFAULT_EXPERIMENTAL_CALENDAR,

            downloadFolderMode = p.readEnum(DOWNLOAD_FOLDER_MODE, UiSettingModel.DEFAULT_DOWNLOAD_FOLDER_MODE),
            addServiceName = p[ADD_SERVICE_NAME] ?: UiSettingModel.DEFAULT_ADD_SERVICE_NAME,
            useExternalMetaData = p[USE_EXTERNAL_METADATA] ?: UiSettingModel.USE_EXTERNAL_METADATA,
            videoPreviewServerUrl = p[VIDEO_PREVIEW_SERVER_URL] ?: UiSettingModel.DEFAULT_VIDEO_PREVIEW_SERVER_URL,
            videoPreviewAspectRatio = p.readEnum(
                VIDEO_PREVIEW_ASPECT_RATIO,
                UiSettingModel.DEFAULT_VIDEO_PREVIEW_ASPECT_RATIO
            ),
            cropVideoPreview = p[CROP_VIDEO_PREVIEW] ?: UiSettingModel.DEFAULT_CROP_VIDEO_PREVIEW,
            cropPostPreviewVideo = p[CROP_POST_PREVIEW_VIDEO] ?: UiSettingModel.DEFAULT_CROP_POST_PREVIEW_VIDEO,
            creatorsGithubRateBannerInstallTsMs = p[CREATORS_GITHUB_RATE_BANNER_INSTALL_TS_MS] ?: 0L,
            creatorsGithubRateBannerDisabled = p[CREATORS_GITHUB_RATE_BANNER_DISABLED] ?: false,
        )
    }

    /** Debug: пропустить проверку API при входе */
    override suspend fun setSkipApiCheckOnLogin(value: Boolean) {
        dataStore.edit {
            it[SKIP_API_CHECK_ON_LOGIN] = value
        }
    }

    /** Режим отображения сайта */
    override suspend fun setSiteDisplayMode(value: SiteDisplayMode) {
        dataStore.edit {
            it[SITE_DISPLAY_MODE] = value.name
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

    override suspend fun setCreatorProfileTabsOrder(value: List<CreatorProfileTabKey>) {
        val normalized = value.normalizedTabOrder()
        dataStore.edit { it[CREATOR_PROFILE_TABS_ORDER] = normalized.joinToString(",") { it.name } }
    }

    override suspend fun setCreatorProfileHiddenTabs(value: Set<CreatorProfileTabKey>) {
        val normalized = value.normalizedHiddenTabs()
        dataStore.edit {
            it[CREATOR_PROFILE_HIDDEN_TABS] = normalized.joinToString(",") { tab -> tab.name }
        }
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

    /** Тема приложения */
    override suspend fun setAppThemeMode(value: AppThemeMode) {
        dataStore.edit { it[APP_THEME_MODE] = value.name }
    }

    /** Формат даты в приложении */
    override suspend fun setDateFormatMode(value: DateFormatMode) {
        dataStore.edit { it[DATE_FORMAT_MODE] = value.name }
    }

    /** Размер постов в сетке */
    override suspend fun setPostsSize(value: PostsSize) {
        dataStore.edit { it[POSTS_SIZE] = value.name }
    }

    override suspend fun setProfilePostsGridSize(value: PostsSize) {
        dataStore.edit { it[PROFILE_POSTS_GRID_SIZE] = value.name }
    }

    override suspend fun setFavoritePostsGridSize(value: PostsSize) {
        dataStore.edit { it[FAVORITE_POSTS_GRID_SIZE] = value.name }
    }

    override suspend fun setPopularPostsGridSize(value: PostsSize) {
        dataStore.edit { it[POPULAR_POSTS_GRID_SIZE] = value.name }
    }

    override suspend fun setTagsPostsGridSize(value: PostsSize) {
        dataStore.edit { it[TAGS_POSTS_GRID_SIZE] = value.name }
    }

    override suspend fun setSearchPostsGridSize(value: PostsSize) {
        dataStore.edit { it[SEARCH_POSTS_GRID_SIZE] = value.name }
    }

    /** Размер кэша картинок (MB) */
    override suspend fun setCoilCacheSizeMb(value: Int) {
        dataStore.edit { it[COIL_CACHE_SIZE_MB] = value.coerceAtLeast(0) }
    }

    /** Показывать ли превью видео */
    override suspend fun setShowPreviewVideo(value: Boolean) {
        dataStore.edit { it[SHOW_PREVIEW_VIDEO] = value }
    }

    /** Автовоспроизведение видео в Community/Discord */
    override suspend fun setAutoplayCommunityVideo(value: Boolean) {
        dataStore.edit { it[AUTOPLAY_COMMUNITY_VIDEO] = value }
    }

    /** Начальное состояние Reverse в Discord Community */
    override suspend fun setDiscordCommunityReverseOrderDefault(value: Boolean) {
        dataStore.edit { it[DISCORD_COMMUNITY_REVERSE_ORDER_DEFAULT] = value }
    }

    /** Блюрить все картинки */
    override suspend fun setBlurImages(value: Boolean) {
        dataStore.edit { it[BLUR_IMAGES] = value }
    }

    /** Показывать кнопку скачивания на миниатюрах изображений в посте */
    override suspend fun setShowImagePreviewDownloadAction(value: Boolean) {
        dataStore.edit { it[SHOW_IMAGE_PREVIEW_DOWNLOAD_ACTION] = value }
    }

    /** Показывать кнопку шаринга на миниатюрах изображений в посте */
    override suspend fun setShowImagePreviewShareAction(value: Boolean) {
        dataStore.edit { it[SHOW_IMAGE_PREVIEW_SHARE_ACTION] = value }
    }

    /** Показывать комментарии в посте */
    override suspend fun setShowCommentsInPost(value: Boolean) {
        dataStore.edit { it[SHOW_COMMENTS_IN_POST] = value }
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

    override suspend fun setVideoPreviewServerUrl(value: String) {
        dataStore.edit { it[VIDEO_PREVIEW_SERVER_URL] = value }
    }

    override suspend fun setVideoPreviewAspectRatio(value: VideoPreviewAspectRatio) {
        dataStore.edit { it[VIDEO_PREVIEW_ASPECT_RATIO] = value.name }
    }

    override suspend fun setCropVideoPreview(value: Boolean) {
        dataStore.edit { it[CROP_VIDEO_PREVIEW] = value }
    }

    override suspend fun setCropPostPreviewVideo(value: Boolean) {
        dataStore.edit { it[CROP_POST_PREVIEW_VIDEO] = value }
    }

    /** Сохранить timestamp первой инициализации баннера оценки в Creators (ms). */
    override suspend fun setCreatorsGithubRateBannerInstallTsMs(value: Long) {
        dataStore.edit { it[CREATORS_GITHUB_RATE_BANNER_INSTALL_TS_MS] = value }
    }

    /** Отключить/включить показ баннера оценки приложения на GitHub в Creators. */
    override suspend fun setCreatorsGithubRateBannerDisabled(value: Boolean) {
        dataStore.edit { it[CREATORS_GITHUB_RATE_BANNER_DISABLED] = value }
    }
}

object UiSettingKey {
    val SKIP_API_CHECK_ON_LOGIN = booleanPreferencesKey("SKIP_API_CHECK_ON_LOGIN")
    val SITE_DISPLAY_MODE = stringPreferencesKey("SITE_DISPLAY_MODE")
    val CREATORS_VIEW_MODE = stringPreferencesKey("CREATORS_VIEW_MODE")
    val CREATORS_FAVORITE_VIEW_MODE = stringPreferencesKey("CREATORS_FAVORITE_VIEW_MODE")

    val PROFILE_POSTS_VIEW_MODE = stringPreferencesKey("PROFILE_POSTS_VIEW_MODE")
    val FAVORITE_POSTS_VIEW_MODE = stringPreferencesKey("FAVORITE_POSTS_VIEW_MODE")
    val POPULAR_POSTS_VIEW_MODE = stringPreferencesKey("POPULAR_POSTS_VIEW_MODE")
    val TAGS_POSTS_VIEW_MODE = stringPreferencesKey("TAGS_POSTS_VIEW_MODE")
    val SEARCH_POSTS_VIEW_MODE = stringPreferencesKey("SEARCH_POSTS_VIEW_MODE")
    val CREATOR_PROFILE_TABS_ORDER = stringPreferencesKey("CREATOR_PROFILE_TABS_ORDER")
    val CREATOR_PROFILE_HIDDEN_TABS = stringPreferencesKey("CREATOR_PROFILE_HIDDEN_TABS")

    val SUGGEST_RANDOM_AUTHORS = booleanPreferencesKey("SUGGEST_RANDOM_AUTHORS")
    val TRANSLATE_TARGET = stringPreferencesKey("TRANSLATE_TARGET")
    val RANDOM_BUTTON_PLACEMENT = stringPreferencesKey("RANDOM_BUTTON_PLACEMENT")
    val TRANSLATE_LANGUAGE_TAG = stringPreferencesKey("TRANSLATE_LANGUAGE")
    val APP_THEME_MODE = stringPreferencesKey("APP_THEME_MODE")

    val DATE_FORMAT_MODE = stringPreferencesKey("DATE_FORMAT_MODE")

    val POSTS_SIZE = stringPreferencesKey("POSTS_SIZE")
    val PROFILE_POSTS_GRID_SIZE = stringPreferencesKey("PROFILE_POSTS_GRID_SIZE")
    val FAVORITE_POSTS_GRID_SIZE = stringPreferencesKey("FAVORITE_POSTS_GRID_SIZE")
    val POPULAR_POSTS_GRID_SIZE = stringPreferencesKey("POPULAR_POSTS_GRID_SIZE")
    val TAGS_POSTS_GRID_SIZE = stringPreferencesKey("TAGS_POSTS_GRID_SIZE")
    val SEARCH_POSTS_GRID_SIZE = stringPreferencesKey("SEARCH_POSTS_GRID_SIZE")

    val SHOW_PREVIEW_VIDEO = booleanPreferencesKey("SHOW_PREVIEW_VIDEO")
    val AUTOPLAY_COMMUNITY_VIDEO = booleanPreferencesKey("AUTOPLAY_COMMUNITY_VIDEO")
    val DISCORD_COMMUNITY_REVERSE_ORDER_DEFAULT =
        booleanPreferencesKey("DISCORD_COMMUNITY_REVERSE_ORDER_DEFAULT")
    val BLUR_IMAGES = booleanPreferencesKey("BLUR_IMAGES")
    val SHOW_IMAGE_PREVIEW_ACTION = booleanPreferencesKey("SHOW_IMAGE_PREVIEW_ACTIONS")
    val SHOW_IMAGE_PREVIEW_DOWNLOAD_ACTION = booleanPreferencesKey("SHOW_IMAGE_PREVIEW_DOWNLOAD_ACTION")
    val SHOW_IMAGE_PREVIEW_SHARE_ACTION = booleanPreferencesKey("SHOW_IMAGE_PREVIEW_SHARE_ACTION")
    val SHOW_COMMENTS_IN_POST = booleanPreferencesKey("SHOW_COMMENTS_IN_POST")
    val EXPERIMENTAL_CALENDAR = booleanPreferencesKey("EXPERIMENTAL_CALENDAR")

    val DOWNLOAD_FOLDER_MODE = stringPreferencesKey("DOWNLOAD_FOLDER_MODE")
    val ADD_SERVICE_NAME = booleanPreferencesKey("ADD_SERVICE_NAME")
    val USE_EXTERNAL_METADATA = booleanPreferencesKey("USE_EXTERNAL_METADATA")
    val VIDEO_PREVIEW_SERVER_URL = stringPreferencesKey("VIDEO_PREVIEW_SERVER_URL")
    val VIDEO_PREVIEW_ASPECT_RATIO = stringPreferencesKey("VIDEO_PREVIEW_ASPECT_RATIO")
    val CROP_VIDEO_PREVIEW = booleanPreferencesKey("CROP_VIDEO_PREVIEW")
    val CROP_POST_PREVIEW_VIDEO = booleanPreferencesKey("CROP_POST_PREVIEW_VIDEO")
    val CREATORS_GITHUB_RATE_BANNER_INSTALL_TS_MS =
        longPreferencesKey("CREATORS_GITHUB_RATE_BANNER_INSTALL_TS_MS")
    val CREATORS_GITHUB_RATE_BANNER_DISABLED =
        booleanPreferencesKey("CREATORS_GITHUB_RATE_BANNER_DISABLED")

    val COIL_CACHE_SIZE_MB = intPreferencesKey("COIL_CACHE_SIZE_MB")
}

// ---- helpers ----
private inline fun <reified T : Enum<T>> Preferences.readEnum(
    key: Preferences.Key<String>,
    default: T
): T {
    val raw = this[key] ?: return default
    return runCatching { enumValueOf<T>(raw) }.getOrDefault(default)
}

private fun Preferences.readTabOrder(key: Preferences.Key<String>): List<CreatorProfileTabKey> {
    val raw = this[key] ?: return UiSettingModel.DEFAULT_CREATOR_PROFILE_TABS_ORDER
    val parsed = raw.split(',')
        .asSequence()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .mapNotNull { name -> runCatching { enumValueOf<CreatorProfileTabKey>(name) }.getOrNull() }
        .toList()

    return parsed.normalizedTabOrder()
}

private fun Preferences.readHiddenTabs(key: Preferences.Key<String>): Set<CreatorProfileTabKey> {
    val raw = this[key] ?: return UiSettingModel.DEFAULT_CREATOR_PROFILE_HIDDEN_TABS
    return raw.split(',')
        .asSequence()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .mapNotNull { name -> runCatching { enumValueOf<CreatorProfileTabKey>(name) }.getOrNull() }
        .toSet()
        .normalizedHiddenTabs()
}

private fun List<CreatorProfileTabKey>.normalizedTabOrder(): List<CreatorProfileTabKey> {
    val orderedUnique = LinkedHashSet(this)
    UiSettingModel.DEFAULT_CREATOR_PROFILE_TABS_ORDER.forEach { orderedUnique.add(it) }
    return orderedUnique.toList()
}

private fun Set<CreatorProfileTabKey>.normalizedHiddenTabs(): Set<CreatorProfileTabKey> =
    this.filterTo(mutableSetOf()) { it != CreatorProfileTabKey.POSTS }
