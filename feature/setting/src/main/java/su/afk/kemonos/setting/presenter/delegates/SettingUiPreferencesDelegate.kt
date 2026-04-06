package su.afk.kemonos.setting.presenter.delegates

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.setting.presenter.SettingState
import javax.inject.Inject

class SettingUiPreferencesDelegate @Inject constructor(
    private val uiSetting: IUiSettingUseCase
) {
    fun handle(event: SettingState.Event.ChangeViewSetting, scope: CoroutineScope) {
        when (event) {
            /** Debug: пропустить проверку API при входе */
            is SettingState.Event.ChangeViewSetting.SkipApiCheckOnLogin ->
                scope.launch { uiSetting.setSkipApiCheckOnLogin(event.value) }

            /** Показывать Kemono */
            is SettingState.Event.ChangeViewSetting.ShowKemono ->
                scope.launch { uiSetting.setShowKemono(event.value) }

            /** Показывать Coomer */
            is SettingState.Event.ChangeViewSetting.ShowCoomer ->
                scope.launch { uiSetting.setShowCoomer(event.value) }

            /** Основной сайт по умолчанию */
            is SettingState.Event.ChangeViewSetting.DefaultSite ->
                scope.launch { uiSetting.setDefaultSite(event.value) }

            /** Режим отображения сайта */
            is SettingState.Event.ChangeViewSetting.SiteDisplayModeChanged ->
                scope.launch { uiSetting.setSiteDisplayMode(event.value) }

            /** Режим отображения FAB */
            is SettingState.Event.ChangeViewSetting.FabVisibilityModeChanged ->
                scope.launch { uiSetting.setFabVisibilityMode(event.value) }

            /** Предлагать рандомных авторов */
            is SettingState.Event.ChangeViewSetting.SuggestRandomAuthors ->
                scope.launch { uiSetting.setSuggestRandomAuthors(event.value) }

            /** Вид отображения авторов  */
            is SettingState.Event.ChangeViewSetting.CreatorsViewMode ->
                scope.launch { uiSetting.setCreatorsViewMode(event.value) }

            /** Вид отображения авторов избранное */
            is SettingState.Event.ChangeViewSetting.CreatorsFavoriteViewMode ->
                scope.launch { uiSetting.setCreatorsFavoriteViewMode(event.value) }

            /** Посты: профиль */
            is SettingState.Event.ChangeViewSetting.ProfilePostsViewMode ->
                scope.launch { uiSetting.setProfilePostsViewMode(event.value) }

            /** Посты: избранное */
            is SettingState.Event.ChangeViewSetting.FavoritePostsViewMode ->
                scope.launch { uiSetting.setFavoritePostsViewMode(event.value) }

            /** Посты: популярное */
            is SettingState.Event.ChangeViewSetting.PopularPostsViewMode ->
                scope.launch { uiSetting.setPopularPostsViewMode(event.value) }

            /** Посты: теги */
            is SettingState.Event.ChangeViewSetting.TagsPostsViewMode ->
                scope.launch { uiSetting.setTagsPostsViewMode(event.value) }

            /** Посты: поиск */
            is SettingState.Event.ChangeViewSetting.SearchPostsViewMode ->
                scope.launch { uiSetting.setSearchPostsViewMode(event.value) }

            /** Размер сетки постов: профиль */
            is SettingState.Event.ChangeViewSetting.ProfilePostsGridSize ->
                scope.launch { uiSetting.setProfilePostsGridSize(event.value) }

            /** Размер сетки постов: избранное */
            is SettingState.Event.ChangeViewSetting.FavoritePostsGridSize ->
                scope.launch { uiSetting.setFavoritePostsGridSize(event.value) }

            /** Размер сетки постов: популярное */
            is SettingState.Event.ChangeViewSetting.PopularPostsGridSize ->
                scope.launch { uiSetting.setPopularPostsGridSize(event.value) }

            /** Размер сетки постов: теги */
            is SettingState.Event.ChangeViewSetting.TagsPostsGridSize ->
                scope.launch { uiSetting.setTagsPostsGridSize(event.value) }

            /** Размер сетки постов: поиск */
            is SettingState.Event.ChangeViewSetting.SearchPostsGridSize ->
                scope.launch { uiSetting.setSearchPostsGridSize(event.value) }

            is SettingState.Event.ChangeViewSetting.EditCreatorProfileTabsOrder ->
                scope.launch { uiSetting.setCreatorProfileTabsOrder(event.value) }
            is SettingState.Event.ChangeViewSetting.EditCreatorProfileHiddenTabs ->
                scope.launch { uiSetting.setCreatorProfileHiddenTabs(event.value) }

            /** Способ перевода */
            is SettingState.Event.ChangeViewSetting.EventTranslateTarget ->
                scope.launch { uiSetting.setTranslateTarget(event.value) }

            /** Где показывать кнопку "рандом" */
            is SettingState.Event.ChangeViewSetting.EventRandomButtonPlacement ->
                scope.launch { uiSetting.setRandomButtonPlacement(event.value) }

            /** Язык, на который переводим */
            is SettingState.Event.ChangeViewSetting.TranslateLanguageTag ->
                scope.launch { uiSetting.setTranslateLanguageTag(event.value) }

            /** Тема приложения */
            is SettingState.Event.ChangeViewSetting.EventAppThemeMode ->
                scope.launch { uiSetting.setAppThemeMode(event.value) }

            /** Формат даты в приложении */
            is SettingState.Event.ChangeViewSetting.EventDateFormatMode ->
                scope.launch { uiSetting.setDateFormatMode(event.value) }

            /** Размер кэша картинок (MB) */
            is SettingState.Event.ChangeViewSetting.CoilCacheSizeMb ->
                scope.launch { uiSetting.setCoilCacheSizeMb(event.value) }

            /** Размер постов в сетке */
            is SettingState.Event.ChangeViewSetting.EditPostsSize ->
                scope.launch { uiSetting.setPostsSize(event.value) }

            /** Показывать превью видео */
            is SettingState.Event.ChangeViewSetting.ShowPreviewVideo ->
                scope.launch { uiSetting.setShowPreviewVideo(event.value) }

            /** Формат карточки превью видео */
            is SettingState.Event.ChangeViewSetting.VideoPreviewAspectRatioChanged ->
                scope.launch { uiSetting.setVideoPreviewAspectRatio(event.value) }

            /** Кропить превью видео по размеру карточки */
            is SettingState.Event.ChangeViewSetting.CropVideoPreview ->
                scope.launch { uiSetting.setCropVideoPreview(event.value) }

            /** Кропить превью видео в карточках постов */
            is SettingState.Event.ChangeViewSetting.CropPostPreviewVideo ->
                scope.launch { uiSetting.setCropPostPreviewVideo(event.value) }

            /** Автовоспроизведение видео в Community/Discord */
            is SettingState.Event.ChangeViewSetting.AutoplayCommunityVideo ->
                scope.launch { uiSetting.setAutoplayCommunityVideo(event.value) }

            /** Начальное состояние Reverse в Discord Community */
            is SettingState.Event.ChangeViewSetting.DiscordCommunityReverseOrderDefault ->
                scope.launch { uiSetting.setDiscordCommunityReverseOrderDefault(event.value) }

            /** Блюрить все картинки */
            is SettingState.Event.ChangeViewSetting.BlurImages ->
                scope.launch { uiSetting.setBlurImages(event.value) }

            /** Показывать кнопку скачивания на миниатюрах изображений в посте */
            is SettingState.Event.ChangeViewSetting.ShowImagePreviewDownloadAction ->
                scope.launch { uiSetting.setShowImagePreviewDownloadAction(event.value) }

            /** Показывать кнопку шаринга на миниатюрах изображений в посте */
            is SettingState.Event.ChangeViewSetting.ShowImagePreviewShareAction ->
                scope.launch { uiSetting.setShowImagePreviewShareAction(event.value) }

            /** Показывать комментарии в посте */
            is SettingState.Event.ChangeViewSetting.ShowCommentsInPost ->
                scope.launch { uiSetting.setShowCommentsInPost(event.value) }

            /** Вид папок для скачивания */
            is SettingState.Event.ChangeViewSetting.EditDownloadFolderMode ->
                scope.launch { uiSetting.setDownloadFolderMode(event.value) }

            /** Добавлять префикс сервиса при скачивании */
            is SettingState.Event.ChangeViewSetting.AddServiceName ->
                scope.launch { uiSetting.setAddServiceName(event.value) }

            /** Использовать внешнее хранилище метадатнных */
            is SettingState.Event.ChangeViewSetting.UseExternalMetaData ->
                scope.launch { uiSetting.setUseExternalMetaData(event.value) }

            /** Экспериментальный календарь поиска популярных постов */
            is SettingState.Event.ChangeViewSetting.ExperimentalCalendar ->
                scope.launch { uiSetting.setExperimentalCalendar(event.value) }
        }
    }
}
