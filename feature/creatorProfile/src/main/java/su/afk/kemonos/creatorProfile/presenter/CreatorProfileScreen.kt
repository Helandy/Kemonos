package su.afk.kemonos.creatorProfile.presenter

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import su.afk.kemonos.common.components.button.FavoriteActionButton
import su.afk.kemonos.common.components.creator.header.CreatorHeader
import su.afk.kemonos.common.components.posts.PostsContentPaging
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.common.shared.ShareActions
import su.afk.kemonos.common.toast.toast
import su.afk.kemonos.common.utilsUI.KemonosPreviewScreen
import su.afk.kemonos.creatorProfile.presenter.CreatorProfileState.*
import su.afk.kemonos.creatorProfile.presenter.model.ProfileTab
import su.afk.kemonos.creatorProfile.presenter.view.*
import su.afk.kemonos.creatorProfile.presenter.view.discordProfile.DiscordProfilePlaceholder
import su.afk.kemonos.deepLink.utils.openUrlPreferChrome
import su.afk.kemonos.domain.models.PostDomain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatorScreen(state: State, onEvent: (Event) -> Unit, effect: Flow<Effect>) {
    val profile = state.profile
    val context = LocalContext.current
    val posts = state.profilePosts.collectAsLazyPagingItems()

    val postsRefreshing = posts.loadState.refresh is LoadState.Loading

    LaunchedEffect(effect) {
        effect.collect { effect ->
            when (effect) {
                is Effect.OpenUrl -> openUrlPreferChrome(context, effect.url)
                is Effect.ShowToast -> context.toast(effect.message)
                is Effect.CopyPostLink -> ShareActions.copyToClipboard(
                    context,
                    "Profile link",
                    effect.message
                )
            }
        }
    }

    val pullState = rememberPullToRefreshState()
    val refreshing = (postsRefreshing || state.loading) && !state.isDiscordProfile

    BaseScreen(
        isScroll = false,
        topBarScroll = TopBarScroll.EnterAlways,
        topBar = {
            if (profile == null) return@BaseScreen
                CreatorHeader(
                    dateMode = state.uiSettingModel.dateFormatMode,
                    service = profile.service,
                    creatorId = profile.id,
                    creatorName = profile.name,
                    updated = profile.updated,
                    showSearchButton = true,
                    showInfoButton = true,
                    onSearchClick = { onEvent(Event.ToggleSearch) },
                    onShareClick = { onEvent(Event.CopyProfileLink) },
                    onClickHeader = null,
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

                ProfileTabsBar(
                    tabs = state.showTabs,
                    selectedTab = state.selectedTab,
                    onTabSelected = { tab ->
                        onEvent(Event.TabChanged(tab))
                    },
                    currentTag = state.currentTag,
                    onTagClear = { onEvent(Event.ClearTag) }
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

        PullToRefreshBox(
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
            dms = state.dmList,
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
