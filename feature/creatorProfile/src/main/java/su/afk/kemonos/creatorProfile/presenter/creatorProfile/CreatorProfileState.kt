package su.afk.kemonos.creatorProfile.presenter.creatorProfile

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.serialization.Serializable
import su.afk.kemonos.creatorProfile.api.domain.models.profileAnnouncements.ProfileAnnouncement
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityChannel
import su.afk.kemonos.creatorProfile.api.domain.models.profileDms.Dm
import su.afk.kemonos.creatorProfile.api.domain.models.profileFanCards.ProfileFanCard
import su.afk.kemonos.creatorProfile.api.domain.models.profileLinks.ProfileLink
import su.afk.kemonos.creatorProfile.api.domain.models.profileSimilar.SimilarCreator
import su.afk.kemonos.creatorProfile.navigation.CreatorDestination
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.model.ProfileTab
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.domain.models.PostDomain.Companion.stableKey
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
        val similarCreators: List<SimilarCreator> = emptyList(),
        val communityChannels: List<CommunityChannel> = emptyList(),

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
        val isInBlacklist: Boolean = false,

        val uiSettingModel: UiSettingModel = UiSettingModel(),

        /** Массовая загрузка: посты в порядке выбора */
        val selectedPosts: List<PostDomain> = emptyList(),
        val selectionMode: Boolean = false,
        val batchDownloadDialogVisible: Boolean = false,
        val batchDownloadFolder: String = "",
        val batchDownloadIncludeCovers: Boolean = true,
        val batchDownloadInProgress: Boolean = false,
    ) : UiState {

        /** Ключ поста -> его номер в выборе, для бейджа на карточке. */
        val selectedOrder: Map<String, Int>
            get() = selectedPosts.withIndex().associate { (index, post) ->
                post.stableKey() to index + 1
            }
    }

    sealed interface Event : UiEvent {

        /** жизненный цикл */
        data object Retry : Event
        data object PullRefresh : Event

        /** навигация/шары */
        data object Back : Event
        data object CopyProfileLink : Event
        data class OpenCreatorPlatformLink(val url: String) : Event
        data object ToggleBlacklist : Event

        data class OpenImage(val url: String) : Event
        data class OpenLinkProfile(val link: ProfileLink) : Event
        data class OpenPost(val post: PostDomain) : Event

        /** табы/фильтры */
        data class TabChanged(val tab: ProfileTab) : Event
        data class OpenCommunityChannel(val channel: CommunityChannel) : Event
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

        /** массовая загрузка */
        data class PostLongClicked(val post: PostDomain) : Event
        data object StartSelection : Event
        data object ExitSelection : Event
        data object OpenBatchDownloadDialog : Event
        data object DismissBatchDownloadDialog : Event
        data class BatchDownloadFolderChanged(val value: String) : Event
        data class BatchDownloadIncludeCoversChanged(val value: Boolean) : Event
        data object ConfirmBatchDownload : Event
    }

    sealed interface Effect : UiEffect {
        data class OpenUrl(val url: String) : Effect
        data class ShowToast(val message: String) : Effect
        data class CopyPostLink(val message: String) : Effect
        data object AddedToBlacklist : Effect
        data object AlreadyInBlacklist : Effect
        data object RemovedFromBlacklist : Effect
        data class BatchDownloadStarted(val works: Int) : Effect
    }
}

@Serializable
internal data class CreatorProfilePersistedState(
    val service: String,
    val id: String,
    val selectedTab: ProfileTab = ProfileTab.POSTS,
    val currentTag: Tag? = null,
    val discordUrlOpened: Boolean = false,
) {
    companion object {
        internal fun fromDest(dest: CreatorDestination.CreatorProfile): CreatorProfilePersistedState =
            CreatorProfilePersistedState(
                service = dest.service,
                id = dest.id,
                currentTag = dest.tag,
            )
    }
}

internal fun CreatorProfilePersistedState.toState(): CreatorProfileState.State =
    CreatorProfileState.State(
        service = service,
        id = id,
        selectedTab = selectedTab,
        currentTag = currentTag,
        discordUrlOpened = discordUrlOpened,
    )

internal fun CreatorProfileState.State.toPersistedState(): CreatorProfilePersistedState =
    CreatorProfilePersistedState(
        service = service,
        id = id,
        selectedTab = selectedTab,
        currentTag = currentTag,
        discordUrlOpened = discordUrlOpened,
    )
