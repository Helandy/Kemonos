package su.afk.kemonos.profile.presenter.setting

import su.afk.kemonos.common.presenter.baseViewModel.UiEffect
import su.afk.kemonos.common.presenter.baseViewModel.UiEvent
import su.afk.kemonos.common.presenter.baseViewModel.UiState
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.model.CacheTimeUi
import su.afk.kemonos.preferences.ui.*

internal class SettingState {
    data class State(
        val loading: Boolean = true,

        val appVersion: String = "",

        val kemonoUrl: String = "",
        val coomerUrl: String = "",

        val inputKemonoDomain: String = "",
        val inputCoomerDomain: String = "",

        val isSaving: Boolean = false,
        val saveSuccess: Boolean = false,

        val uiSettingModel: UiSettingModel = UiSettingModel(),

        /** cache */
        val tagsKemonoCache: CacheTimeUi = CacheTimeUi(null, null, false),
        val tagsCoomerCache: CacheTimeUi = CacheTimeUi(null, null, false),

        val creatorsKemonoCache: CacheTimeUi = CacheTimeUi(null, null, false),
        val creatorsCoomerCache: CacheTimeUi = CacheTimeUi(null, null, false),

        val postContentsCache: CacheTimeUi = CacheTimeUi(null, null, false),
        val creatorPostsCache: CacheTimeUi = CacheTimeUi(null, null, false),
        val popularKemonoCache: CacheTimeUi = CacheTimeUi(null, null, false),
        val favPostsKemonoCache: CacheTimeUi = CacheTimeUi(null, null, false),
        val favCreatorsKemonoCache: CacheTimeUi = CacheTimeUi(null, null, false),
        val creatorProfilesCache: CacheTimeUi = CacheTimeUi(null, null, false),

        val clearInProgress: Boolean = false,
        val clearSuccess: Boolean? = null,
    ) : UiState

    sealed interface Event : UiEvent {
        data object Back : Event

        sealed interface ChangeViewSetting : Event {
            data class SkipApiCheckOnLogin(val value: Boolean) : ChangeViewSetting

            data class SuggestRandomAuthors(val value: Boolean) : ChangeViewSetting

            data class CreatorsViewMode(val value: CreatorViewMode) : ChangeViewSetting
            data class CreatorsFavoriteViewMode(val value: CreatorViewMode) : ChangeViewSetting

            data class ProfilePostsViewMode(val value: PostsViewMode) : ChangeViewSetting
            data class FavoritePostsViewMode(val value: PostsViewMode) : ChangeViewSetting
            data class PopularPostsViewMode(val value: PostsViewMode) : ChangeViewSetting
            data class TagsPostsViewMode(val value: PostsViewMode) : ChangeViewSetting
            data class SearchPostsViewMode(val value: PostsViewMode) : ChangeViewSetting

            data class EventTranslateTarget(val value: TranslateTarget) : ChangeViewSetting
            data class EventRandomButtonPlacement(val value: RandomButtonPlacement) : ChangeViewSetting
            data class TranslateLanguageTag(val value: String) : ChangeViewSetting
            data class EventDateFormatMode(val value: DateFormatMode) : ChangeViewSetting

            data class CoilCacheSizeMb(val value: Int) : ChangeViewSetting
            data class PreviewVideoSizeMb(val value: Int) : ChangeViewSetting

            data class EditPostsSize(val value: PostsSize) : ChangeViewSetting
            data class ShowPreviewVideo(val value: Boolean) : ChangeViewSetting
            data class BlurImages(val value: Boolean) : ChangeViewSetting

            data class EditDownloadFolderMode(val value: DownloadFolderMode) : ChangeViewSetting
            data class AddServiceName(val value: Boolean) : ChangeViewSetting
            data class UseExternalMetaData(val value: Boolean) : ChangeViewSetting

            data class ExperimentalCalendar(val value: Boolean) : ChangeViewSetting
        }

        sealed interface ApiSetting : Event {
            data class InputKemonoDomainChanged(val value: String) : ApiSetting
            data class InputCoomerDomainChanged(val value: String) : ApiSetting
            data object SaveUrls : ApiSetting
        }

        sealed interface CacheClearAction : Event {
            object CreatorProfiles : CacheClearAction
            object CreatorPostsPages : CacheClearAction
            object PostContents : CacheClearAction
            object PopularPosts : CacheClearAction
            object FavoritesArtists : CacheClearAction
            object FavoritesPosts : CacheClearAction

            data class Creators(val site: SelectedSite) : CacheClearAction
            data class Tags(val site: SelectedSite) : CacheClearAction
        }
    }

    sealed interface Effect : UiEffect {
        data class OpenUrl(val url: String) : Effect
    }
}
