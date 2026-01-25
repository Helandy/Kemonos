package su.afk.kemonos.creatorProfile.presenter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.baseScreen.StandardTopBar
import su.afk.kemonos.common.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.common.shared.ShareActions
import su.afk.kemonos.common.shared.view.SharedActionButton
import su.afk.kemonos.common.util.toast
import su.afk.kemonos.common.view.button.FavoriteActionButton
import su.afk.kemonos.common.view.creator.header.CreatorHeader
import su.afk.kemonos.common.view.postsScreen.paging.PostsTabContent
import su.afk.kemonos.creatorProfile.presenter.model.ProfileTab
import su.afk.kemonos.creatorProfile.presenter.view.*
import su.afk.kemonos.creatorProfile.presenter.view.discordProfile.DiscordProfilePlaceholder
import su.afk.kemonos.deepLink.utils.openUrlPreferChrome
import su.afk.kemonos.domain.models.PostDomain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatorScreen(
    viewModel: CreatorProfileViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val profile = state.profile
    val context = LocalContext.current
    val posts = state.profilePosts.collectAsLazyPagingItems()

    val postsRefreshing = posts.loadState.refresh is LoadState.Loading
    val gridState = rememberSaveable(saver = LazyGridState.Saver) {
        LazyGridState()
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CreatorProfileEffect.OpenUrl -> openUrlPreferChrome(context, effect.url)
                is CreatorProfileEffect.ShowToast -> context.toast(effect.message)
                is CreatorProfileEffect.CopyPostLink -> ShareActions.copyToClipboard(
                    context,
                    "Profile link",
                    effect.message
                )
            }
        }
    }

    var lastIndex by remember { mutableIntStateOf(0) }
    var lastOffset by remember { mutableIntStateOf(0) }
    var headerVisible by remember { mutableStateOf(true) }

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.firstVisibleItemIndex to gridState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                val threshold = 10
                val scrollingDown = index > lastIndex || (index == lastIndex && offset - lastOffset > threshold)
                val scrollingUp = index < lastIndex || (index == lastIndex && lastOffset - offset > threshold)

                // правило:
                // вниз -> скрываем, вверх -> показываем
                if (scrollingDown) headerVisible = false
                else if (scrollingUp) headerVisible = true

                lastIndex = index
                lastOffset = offset
            }
    }

    val pullState = rememberPullToRefreshState()
    val refreshing = (postsRefreshing || state.loading) && !state.isDiscordProfile

    BaseScreen(
        isScroll = false,
        topBarScroll = TopBarScroll.EnterAlways,
        topBar = { scrollBehavior ->
            StandardTopBar(
                scrollBehavior = scrollBehavior,
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
            ) {
                if (profile == null) return@StandardTopBar

                CreatorHeader(
                    dateMode = state.uiSettingModel.dateFormatMode,
                    service = profile.service,
                    creatorId = profile.id,
                    creatorName = profile.name,
                    updated = profile.updated,
                    showSearchButton = true,
                    showInfoButton = true,
                    onSearchClick = { viewModel.toggleSearch() },
                    onClickHeader = null,
                )

                SearchBar(
                    searchText = state.searchText,
                    onSearchTextChange = viewModel::setSearchText,
                    visible = state.isSearchVisible,
                    onClose = { viewModel.closeSearch() }
                )

                ProfileTabsBar(
                    tabs = state.showTabs,
                    selectedTab = state.selectedTab,
                    onTabSelected = { tab ->
                        viewModel.onTabChanged(tab)
                    },
                    currentTag = state.currentTag,
                    onTagClear = { viewModel.clearTag() }
                )
            }
        },
        contentModifier = Modifier.padding(horizontal = 8.dp),
        floatingActionButtonStart = {
            if (!state.loading) {
                SharedActionButton(
                    onClick = { viewModel.copyProfileLink() }
                )
            }
        },
        floatingActionButtonBottomPadding = 12.dp,
        floatingActionButtonEnd = {
            if (state.isFavoriteShowButton && state.loading.not()) {
                FavoriteActionButton(
                    enabled = !state.favoriteActionLoading,
                    isFavorite = state.isFavorite,
                    onFavoriteClick = { viewModel.onFavoriteClick() }
                )
            }
        },
        isLoading = (state.loading || postsRefreshing) && !state.isDiscordProfile,
        isEmpty = state.profile == null && !state.loading && !postsRefreshing,
        onRetry = { viewModel.getProfileInfo() }
    ) {
        if (state.isDiscordProfile) {
            DiscordProfilePlaceholder(
                onBack = { viewModel.back() }
            )
            return@BaseScreen
        }

        if (profile == null) return@BaseScreen

        PullToRefreshBox(
            state = pullState,
            isRefreshing = refreshing,
            onRefresh = { viewModel.onPullRefresh() }
        ) {
            SelectedTab(
                state = state,
                viewModel = viewModel,
                posts = posts,
                gridState = gridState,
            )
        }
    }
}

/** Контент выбранной вкладки */
@Composable
private fun SelectedTab(
    state: CreatorProfileState,
    viewModel: CreatorProfileViewModel,
    posts: LazyPagingItems<PostDomain>,
    gridState: LazyGridState,
) {
    when (state.selectedTab) {
        ProfileTab.POSTS -> PostsTabContent(
            dateMode = state.uiSettingModel.dateFormatMode,
            postsViewMode = state.uiSettingModel.profilePostsViewMode,
            posts = posts,
            gridState = gridState,
            currentTag = state.currentTag,
            onPostClick = viewModel::navigateToPost,
            onRetry = { posts.retry() },
            parseError = viewModel::parseError,
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
                viewModel.navigateToOpenImage(imgUrl)
            }
        )

        ProfileTab.TAGS -> TagsScreen(
            tags = state.profileTags,
            onTagClick = { tag ->
                viewModel.clickTag(tag)
            }
        )

        ProfileTab.LINKS -> ProfileLinksScreen(
            dateMode = state.uiSettingModel.dateFormatMode,
            links = state.profileLinks,
            onClick = { openProfile ->
                viewModel.navigateToLinkProfile(openProfile)
            }
        )
    }
}


