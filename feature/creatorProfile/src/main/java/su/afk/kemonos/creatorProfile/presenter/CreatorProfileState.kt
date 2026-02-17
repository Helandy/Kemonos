package su.afk.kemonos.creatorProfile.presenter

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.creatorProfile.api.domain.models.profileAnnouncements.ProfileAnnouncement
import su.afk.kemonos.creatorProfile.api.domain.models.profileDms.Dm
import su.afk.kemonos.creatorProfile.api.domain.models.profileFanCards.ProfileFanCard
import su.afk.kemonos.creatorProfile.api.domain.models.profileLinks.ProfileLink
import su.afk.kemonos.creatorProfile.presenter.model.ProfileTab
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.domain.models.Profile
import su.afk.kemonos.domain.models.Tag
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.components.posts.filter.PostMediaFilter
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class CreatorProfileState {

    data class State(
        val loading: Boolean = false,

        var service: String = "",
        var id: String = "",
        var page: Int = 0,

        val isDiscordProfile: Boolean = false,
        val discordUrlOpened: Boolean = false,

        /** Информация об авторе */
        val profile: Profile? = null,

        /** Для решения нужен ли запрос на ДС */
        val countDm: Int? = null,
        /** Всего постов автора */
        val countPost: Int? = null,

        /** Контент табов */
        val profilePosts: Flow<PagingData<PostDomain>> = emptyFlow(),
        val announcements: List<ProfileAnnouncement> = emptyList(),
        val fanCardsList: List<ProfileFanCard> = emptyList(),
        val dmList: List<Dm> = emptyList(),
        val profileLinks: List<ProfileLink> = emptyList(),

        /** Табы */
        val showTabs: List<ProfileTab> = listOf(ProfileTab.POSTS),
        val selectedTab: ProfileTab = ProfileTab.POSTS,

        /** Поиск */
        val searchText: String = "",
        val mediaFilter: PostMediaFilter = PostMediaFilter(),
        /** показывать меню поиска */
        val isSearchVisible: Boolean = false,

        /** tags */
        val currentTag: Tag? = null,
        val profileTags: List<Tag> = emptyList(),

        /** в избранном ли автор */
        val isFavoriteShowButton: Boolean = false,
        val isFavorite: Boolean = false,
        val favoriteActionLoading: Boolean = false,

        val uiSettingModel: UiSettingModel = UiSettingModel(),
    ) : UiState

    sealed interface Event : UiEvent {

        /** жизненный цикл */
        data object Retry : Event
        data object PullRefresh : Event

        /** навигация/шары */
        data object Back : Event
        data object CopyProfileLink : Event
        data class OpenCreatorPlatformLink(val url: String) : Event

        data class OpenImage(val url: String) : Event
        data class OpenLinkProfile(val link: ProfileLink) : Event
        data class OpenPost(val post: PostDomain) : Event

        /** табы/фильтры */
        data class TabChanged(val tab: ProfileTab) : Event
        data class TagClicked(val tag: Tag) : Event
        data object ClearTag : Event

        /** поиск */
        data object ToggleSearch : Event
        data object CloseSearch : Event
        data class SearchTextChanged(val text: String) : Event

        /** Фильтры */
        data object ToggleHasVideo : Event
        data object ToggleHasAttachments : Event
        data object ToggleHasImages : Event

        /** избранное */
        data object FavoriteClick : Event
    }

    sealed interface Effect : UiEffect {
        data class OpenUrl(val url: String) : Effect
        data class ShowToast(val message: String) : Effect
        data class CopyPostLink(val message: String) : Effect
    }
}
