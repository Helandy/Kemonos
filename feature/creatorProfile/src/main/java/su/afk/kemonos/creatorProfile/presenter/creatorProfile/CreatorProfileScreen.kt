package su.afk.kemonos.creatorProfile.presenter.creatorProfile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import su.afk.kemonos.creatorProfile.api.domain.models.profileLinks.ProfileLink.Companion.toProfileLink
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.CreatorProfileState.Event
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.CreatorProfileState.State
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.model.ProfileTab
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.view.*
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.view.discordProfile.DiscordProfilePlaceholder
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.view.header.CreatorScreenTopBar
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.view.header.ProfileTabsBar
import su.afk.kemonos.deepLink.utils.openUrlInBrowser
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.components.button.FavoriteActionButton
import su.afk.kemonos.ui.components.dm.DmListScreen
import su.afk.kemonos.ui.components.dm.DmUiItem
import su.afk.kemonos.ui.components.posts.PostsContentPaging
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.ui.preview.KemonosPreviewScreen
import su.afk.kemonos.ui.shared.ShareActions
import su.afk.kemonos.ui.toast.toast
import su.afk.kemonos.utils.creator.buildCreatorPlatformUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatorScreen(
    state: State,
    onEvent: (Event) -> Unit,
    effect: Flow<CreatorProfileState.Effect>
) {
    val profile = state.profile
    val context = LocalContext.current
    val posts = state.profilePosts.collectAsLazyPagingItems()

    val postsRefreshing = posts.loadState.refresh is LoadState.Loading

    LaunchedEffect(effect) {
        effect.collect { effect ->
            when (effect) {
                is CreatorProfileState.Effect.OpenUrl -> openUrlInBrowser(context, effect.url)
                is CreatorProfileState.Effect.ShowToast -> context.toast(effect.message)
                is CreatorProfileState.Effect.CopyPostLink -> ShareActions.copyToClipboard(
                    context,
                    "Profile link",
                    effect.message
                )

                CreatorProfileState.Effect.AddedToBlacklist -> context.toast(context.getString(R.string.author_blacklist_added))
                CreatorProfileState.Effect.RemovedFromBlacklist -> context.toast(context.getString(R.string.author_blacklist_removed))
                CreatorProfileState.Effect.AlreadyInBlacklist -> context.toast(context.getString(R.string.author_blacklist_already_exists))
            }
        }
    }

    val pullState = rememberPullToRefreshState()
    val refreshing = (postsRefreshing || state.loading) && !state.isDiscordProfile
    val platformUrl = remember(profile?.service, profile?.publicId, profile?.id) {
        profile?.let {
            buildCreatorPlatformUrl(
                service = it.service,
                publicId = it.publicId,
                id = it.id
            )
        }
    }

    BaseScreen(
        isScroll = false,
        topBarScroll = TopBarScroll.EnterAlways,
        customTopBar = { scrollBehavior ->
            CreatorScreenTopBar(
                profile = profile,
                dateFormatMode = state.uiSettingModel.dateFormatMode,
                platformUrl = platformUrl,
                scrollBehavior = scrollBehavior,
                onBack = { onEvent(Event.Back) },
                onToggleSearch = { onEvent(Event.ToggleSearch) },
                onShare = { onEvent(Event.CopyProfileLink) },
                onOpenPlatform = { onEvent(Event.OpenCreatorPlatformLink(it)) },
                isInBlacklist = state.isInBlacklist,
                onToggleBlacklist = { onEvent(Event.ToggleBlacklist) },
            )
        },
        contentModifier = Modifier.padding(horizontal = 8.dp),
        floatingActionButtonEnd = {
            if (state.isFavoriteShowButton && state.loading.not()) {
                FavoriteActionButton(
                    enabled = !state.favoriteActionLoading,
                    isFavorite = state.isFavorite,
                    onFavoriteClick = {
                        onEvent(Event.FavoriteClick)
                    }
                )
            }
        },
        isLoading = (state.loading || postsRefreshing) && !state.isDiscordProfile,
        isEmpty = state.profile == null &&
                !state.loading &&
                !postsRefreshing &&
                state.searchText.isBlank() &&
                !state.mediaFilter.isActive,
        onRetry = {
            onEvent(Event.Retry)
        }
    ) {
        if (state.isDiscordProfile) {
            DiscordProfilePlaceholder(
                onBack = { onEvent(Event.Back) },
            )
            return@BaseScreen
        }

        if (profile == null) return@BaseScreen

        ProfileTabsBar(
            tabs = state.showTabs,
            selectedTab = state.selectedTab,
            tabsOrder = state.uiSettingModel.creatorProfileTabsOrder,
            onTabSelected = { tab ->
                onEvent(Event.TabChanged(tab))
            },
            currentTag = state.currentTag,
            onTagClear = { onEvent(Event.ClearTag) }
        )

        SearchBar(
            searchText = state.searchText,
            onSearchTextChange = {
                onEvent(Event.SearchTextChanged(it))
            },
            mediaFilter = state.mediaFilter,
            onToggleHasVideo = { onEvent(Event.ToggleHasVideo) },
            onToggleHasAttachments = { onEvent(Event.ToggleHasAttachments) },
            onToggleHasImages = { onEvent(Event.ToggleHasImages) },
            visible = state.isSearchVisible,
            onClose = {
                onEvent(Event.CloseSearch)
            }
        )

        PullToRefreshBox(
            modifier = Modifier.fillMaxSize(),
            state = pullState,
            isRefreshing = refreshing,
            onRefresh = { onEvent(Event.PullRefresh) }
        ) {
            SelectedTab(
                state = state,
                onEvent = onEvent,
                posts = posts,
            )
        }
    }
}

/** Контент выбранной вкладки */
@Composable
private fun SelectedTab(
    state: State,
    onEvent: (Event) -> Unit,
    posts: LazyPagingItems<PostDomain>,
) {
    when (state.selectedTab) {
        ProfileTab.POSTS -> PostsContentPaging(
            postsViewMode = state.uiSettingModel.profilePostsViewMode,
            uiSettingModel = state.uiSettingModel,
            posts = posts,
            currentTag = state.currentTag,
            onPostClick = {
                onEvent(Event.OpenPost(it))
            },
            onRetry = { posts.retry() },
        )

        ProfileTab.DMS -> DmListScreen(
            dateMode = state.uiSettingModel.dateFormatMode,
            dms = state.dmList.map { dm ->
                DmUiItem(
                    hash = dm.hash,
                    content = dm.content,
                    published = dm.published,
                )
            },
            sortByPublishedDesc = true,
        )

        ProfileTab.ANNOUNCEMENTS -> AnnouncementsScreen(
            dateMode = state.uiSettingModel.dateFormatMode,
            announcements = state.announcements
        )

        ProfileTab.FANCARD -> FanCardGridScreen(
            dateMode = state.uiSettingModel.dateFormatMode,
            fanCards = state.fanCardsList,
            onCardClick = { imgUrl ->
                onEvent(Event.OpenImage(imgUrl))
            }
        )

        ProfileTab.TAGS -> TagsScreen(
            tags = state.profileTags,
            onTagClick = { tag ->
                onEvent(Event.TagClicked(tag))
            }
        )

        ProfileTab.LINKS -> ProfileLinksScreen(
            dateMode = state.uiSettingModel.dateFormatMode,
            links = state.profileLinks,
            onClick = { openProfile ->
                onEvent(Event.OpenLinkProfile(openProfile))
            }
        )

        ProfileTab.SIMILAR -> SimilarCreatorsScreen(
            dateMode = state.uiSettingModel.dateFormatMode,
            creators = state.similarCreators,
            onClick = { creator ->
                onEvent(Event.OpenLinkProfile(creator.toProfileLink()))
            }
        )

        ProfileTab.COMMUNITY -> CommunityScreen(
            channels = state.communityChannels,
            onOpenChannel = { channel ->
                onEvent(Event.OpenCommunityChannel(channel))
            }
        )
    }
}

@Preview("PreviewCreatorScreen")
@Composable
private fun PreviewCreatorScreen() {
    KemonosPreviewScreen {
        CreatorScreen(
            state = State(),
            onEvent = {},
            effect = flowOf()
        )
    }
}
