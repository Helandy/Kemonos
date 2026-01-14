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

internal data class CreatorProfileState(
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
    /** показывать меню поиска */
    val isSearchVisible: Boolean = false,

    /** tags */
    val currentTag: Tag? = null,
    val profileTags: List<Tag> = emptyList(),

    /** в избранном ли автор */
    val isFavoriteShowButton: Boolean = false,
    val isFavorite: Boolean = false,
    val favoriteActionLoading: Boolean = false,
)

sealed interface CreatorProfileEffect {
    data class OpenUrl(val url: String) : CreatorProfileEffect
    data class ShowToast(val message: String) : CreatorProfileEffect
    data class CopyPostLink(val message: String) : CreatorProfileEffect
}