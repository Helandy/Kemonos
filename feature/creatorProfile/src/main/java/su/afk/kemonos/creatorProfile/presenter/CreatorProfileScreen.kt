package su.afk.kemonos.creatorProfile.presenter

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.screens.postsScreen.paging.PostsTabContent
import su.afk.kemonos.common.presenter.views.creator.CreatorHeader
import su.afk.kemonos.common.presenter.views.elements.FavoriteActionButton
import su.afk.kemonos.common.shared.view.SharedActionButton
import su.afk.kemonos.creatorProfile.presenter.model.ProfileTab
import su.afk.kemonos.creatorProfile.presenter.view.*
import su.afk.kemonos.creatorProfile.presenter.view.discordProfile.DiscordProfilePlaceholder
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
                is CreatorProfileEffect.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                    context.startActivity(intent)
                }
            }
        }
    }

    val pullState = rememberPullToRefreshState()
    val refreshing = postsRefreshing || state.loading

    BaseScreen(
        isScroll = false,
        contentModifier = Modifier.padding(horizontal = 8.dp),
        floatingActionButtonStart = {
            if (!state.loading) {
                SharedActionButton(
                    onClick = { viewModel.copyProfileLink(context) }
                )
            }
        },
        floatingActionButtonBottomPadding = 12.dp,
        floatingActionButton = {
            if (state.isFavoriteShowButton && state.loading.not()) {
                FavoriteActionButton(
                    isFavorite = state.isFavorite,
                    onFavoriteClick = { viewModel.onFavoriteClick() }
                )
            }
        },
        isLoading = state.loading || postsRefreshing,
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

        CreatorHeader(
            service = profile.service,
            creatorId = profile.id,
            creatorName = profile.name,
            updated = profile.updated,
            showSearchButton = true,
            showInfoButton = true,
            onSearchClick = { viewModel.toggleSearch() },
            onClickHeader = null
        )

        SearchBar(
            searchText = state.searchText,
            onSearchTextChange = { viewModel.setSearchText(it) },
            visible = state.isSearchVisible,
            onClose = { viewModel.setSearchVisible(false) }
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
            posts = posts,
            gridState = gridState,
            currentTag = state.currentTag,
            onPostClick = viewModel::navigateToPost,
            onRetry = { posts.retry() },
            parseError = viewModel::parseError,
        )

        ProfileTab.DMS -> DmListScreen(
            dms = state.dmList,
        )

        ProfileTab.ANNOUNCEMENTS -> AnnouncementsScreen(state.announcements)

        ProfileTab.FANCARD -> FanCardGridScreen(
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
            links = state.profileLinks,
            onClick = { openProfile ->
                viewModel.navigateToLinkProfile(openProfile)
            }
        )
    }
}


