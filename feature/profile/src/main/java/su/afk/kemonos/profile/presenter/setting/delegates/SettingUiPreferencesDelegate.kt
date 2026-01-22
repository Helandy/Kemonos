package su.afk.kemonos.profile.presenter.setting.delegates

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.profile.presenter.setting.SettingState.Event
import javax.inject.Inject

internal class SettingUiPreferencesDelegate @Inject constructor(
    private val uiSetting: IUiSettingUseCase
) {
    fun handle(event: Event.ChangeViewSetting, scope: CoroutineScope) {
        when (event) {
            /** Debug: пропустить проверку API при входе */
            is Event.ChangeViewSetting.SkipApiCheckOnLogin ->
                scope.launch { uiSetting.setSkipApiCheckOnLogin(event.value) }

            /** Предлагать рандомных авторов */
            is Event.ChangeViewSetting.SuggestRandomAuthors ->
                scope.launch { uiSetting.setSuggestRandomAuthors(event.value) }

            /** Вид отображения авторов  */
            is Event.ChangeViewSetting.CreatorsViewMode ->
                scope.launch { uiSetting.setCreatorsViewMode(event.value) }

            /** Вид отображения авторов избранное */
            is Event.ChangeViewSetting.CreatorsFavoriteViewMode ->
                scope.launch { uiSetting.setCreatorsFavoriteViewMode(event.value) }

            /** Посты: профиль */
            is Event.ChangeViewSetting.ProfilePostsViewMode ->
                scope.launch { uiSetting.setProfilePostsViewMode(event.value) }

            /** Посты: избранное */
            is Event.ChangeViewSetting.FavoritePostsViewMode ->
                scope.launch { uiSetting.setFavoritePostsViewMode(event.value) }

            /** Посты: популярное */
            is Event.ChangeViewSetting.PopularPostsViewMode ->
                scope.launch { uiSetting.setPopularPostsViewMode(event.value) }

            /** Посты: теги */
            is Event.ChangeViewSetting.TagsPostsViewMode ->
                scope.launch { uiSetting.setTagsPostsViewMode(event.value) }

            /** Посты: поиск */
            is Event.ChangeViewSetting.SearchPostsViewMode ->
                scope.launch { uiSetting.setSearchPostsViewMode(event.value) }

            /** Способ перевода */
            is Event.ChangeViewSetting.EventTranslateTarget ->
                scope.launch { uiSetting.setTranslateTarget(event.value) }

            /** Где показывать кнопку "рандом" */
            is Event.ChangeViewSetting.EventRandomButtonPlacement ->
                scope.launch { uiSetting.setRandomButtonPlacement(event.value) }

            /** Язык, на который переводим */
            is Event.ChangeViewSetting.TranslateLanguageTag ->
                scope.launch { uiSetting.setTranslateLanguageTag(event.value) }

            /** Формат даты в приложении */
            is Event.ChangeViewSetting.EventDateFormatMode ->
                scope.launch { uiSetting.setDateFormatMode(event.value) }
        }
    }
}
