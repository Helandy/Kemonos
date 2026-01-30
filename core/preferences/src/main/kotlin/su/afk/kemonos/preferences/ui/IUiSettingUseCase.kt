package su.afk.kemonos.preferences.ui

import kotlinx.coroutines.flow.Flow

interface IUiSettingUseCase {
    val prefs: Flow<UiSettingModel>

    /** Debug: пропустить проверку API при входе */
    suspend fun setSkipApiCheckOnLogin(value: Boolean)

    /** Вид отображения авторов  */
    suspend fun setCreatorsViewMode(value: CreatorViewMode)

    /** Вид отображения авторов избранное */
    suspend fun setCreatorsFavoriteViewMode(value: CreatorViewMode)

    /** Вид отображения постов */
    suspend fun setProfilePostsViewMode(value: PostsViewMode)
    suspend fun setFavoritePostsViewMode(value: PostsViewMode)
    suspend fun setPopularPostsViewMode(value: PostsViewMode)
    suspend fun setTagsPostsViewMode(value: PostsViewMode)
    suspend fun setSearchPostsViewMode(value: PostsViewMode)

    /** Предлагать рандомных авторов */
    suspend fun setSuggestRandomAuthors(value: Boolean)

    /** Способ перевода */
    suspend fun setTranslateTarget(value: TranslateTarget)

    /** Где показывать кнопку "рандом" */
    suspend fun setRandomButtonPlacement(value: RandomButtonPlacement)

    /** Язык, на который переводим ("" = системный) */
    suspend fun setTranslateLanguageTag(value: String)

    /** Формат даты в приложении */
    suspend fun setDateFormatMode(value: DateFormatMode)

    /** Размер постов в сетке */
    suspend fun setPostsSize(value: PostsSize)

    /** Размер кэша картинок (MB) */
    suspend fun setCoilCacheSizeMb(value: Int)

    /** Размер кэша превьюшек (MB) */
    suspend fun setPreviewVideoSizeMb(value: Int)

    /** Показывать превью видео */
    suspend fun setShowPreviewVideo(value: Boolean)

    /** Блюрить все картинки */
    suspend fun setBlurImages(value: Boolean)

    /** Экспериментальный календарь поиска популярных постов */
    suspend fun setExperimentalCalendar(value: Boolean)

    /** Вид папок для скачивания */
    suspend fun setDownloadFolderMode(value: DownloadFolderMode)

    /** Добавлять префикс сервиса при скачивании */
    suspend fun setAddServiceName(value: Boolean)

    /** Использовать внешнее хранилище метадатнных */
    suspend fun setUseExternalMetaData(value: Boolean)
}