package su.afk.kemonos.profile.presenter.favoritePosts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.GroupWork
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.error.error.LocalErrorMapper
import su.afk.kemonos.preferences.ui.PostsSize.Companion.toArrangement
import su.afk.kemonos.preferences.ui.PostsSize.Companion.toDp
import su.afk.kemonos.preferences.ui.PostsViewMode
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.profile.presenter.favoritePosts.FavoritePostsState.*
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.components.creator.CreatorListItem
import su.afk.kemonos.ui.components.posts.PostsContentPaging
import su.afk.kemonos.ui.components.posts.postCard.PostCard
import su.afk.kemonos.ui.components.searchBar.PostsSearchBarWithMediaFilters
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.CenterBackTopBar
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.ui.preview.KemonosPreviewScreen
import su.afk.kemonos.profile.R as ProfileR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoritePostsScreen(
    state: State,
    onEvent: (Event) -> Unit,
    effect: Flow<Effect>,
) {
    LocalErrorMapper.current

    val focusManager = LocalFocusManager.current
    val posts = state.posts.collectAsLazyPagingItems()
    val pullState = rememberPullToRefreshState()
    val pagingIsEmpty = posts.loadState.refresh is LoadState.NotLoading && posts.itemCount == 0

    BaseScreen(
        contentPadding = PaddingValues(horizontal = 8.dp),
        topBarScroll = TopBarScroll.EnterAlways,
        isScroll = false,
        customTopBar = { scrollBehavior ->
            Column {
                CenterBackTopBar(
                    title = stringResource(ProfileR.string.favorite_posts_title),
                    onBack = { onEvent(Event.Back) },
                    scrollBehavior = scrollBehavior,
                )

                PostsSearchBarWithMediaFilters(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    query = state.searchQuery,
                    onQueryChange = { onEvent(Event.SearchQueryChanged(it)) },
                    mediaFilter = state.mediaFilter,
                    onToggleHasVideo = { onEvent(Event.ToggleHasVideo) },
                    onToggleHasAttachments = { onEvent(Event.ToggleHasAttachments) },
                    onToggleHasImages = { onEvent(Event.ToggleHasImages) },
                    label = stringResource(R.string.search),
                    onSearch = { focusManager.clearFocus() }
                )
            }
        },
        isEmpty = pagingIsEmpty,
        onRetry = { onEvent(Event.Load()) },
        floatingActionButtonEnd = {
            FloatingActionButton(
                onClick = { onEvent(Event.ToggleGroupByAuthor) },
                containerColor = if (state.groupByAuthorEnabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
            ) {
                Icon(
                    imageVector = if (state.groupByAuthorEnabled) Icons.Filled.GroupWork else Icons.Filled.Dashboard,
                    contentDescription = "Toggle grouping by author"
                )
            }
        },
    ) {
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            state = pullState,
            isRefreshing = state.loading,
            onRefresh = {
                onEvent(Event.Load(refresh = true))
                posts.refresh()
            },
        ) {
            if (state.groupByAuthorEnabled) {
                FavoritePostsGroupedList(
                    uiSettingModel = state.uiSettingModel,
                    postsViewMode = state.uiSettingModel.favoritePostsViewMode,
                    posts = posts,
                    authorNamesByKey = state.authorNamesByKey,
                    onPostClick = { onEvent(Event.NavigateToPost(it)) },
                    onProfileClick = { service, creatorId ->
                        onEvent(Event.NavigateToProfile(service, creatorId))
                    }
                )
            } else {
                PostsContentPaging(
                    postsViewMode = state.uiSettingModel.favoritePostsViewMode,
                    uiSettingModel = state.uiSettingModel,
                    posts = posts,
                    onPostClick = { onEvent(Event.NavigateToPost(it)) },
                    showFavCount = false,
                    currentTag = null,
                    onRetry = { posts.refresh() },
                )
            }
        }
    }
}

@Composable
private fun FavoritePostsGroupedList(
    uiSettingModel: UiSettingModel,
    postsViewMode: PostsViewMode,
    posts: LazyPagingItems<PostDomain>,
    authorNamesByKey: Map<String, String>,
    onPostClick: (PostDomain) -> Unit,
    onProfileClick: (service: String, creatorId: String) -> Unit,
) {
    val groups = buildAuthorGroups(posts.itemSnapshotList.items, authorNamesByKey)

    when (postsViewMode) {
        PostsViewMode.LIST -> {
            LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                groups.forEachIndexed { groupIndex, group ->
                    item(key = "header:${group.service}:${group.userId}") {
                        Box(modifier = Modifier.padding(top = if (groupIndex == 0) 0.dp else 12.dp, bottom = 8.dp)) {
                            CreatorListItem(
                                dateMode = uiSettingModel.dateFormatMode,
                                service = group.service,
                                id = group.userId,
                                name = group.authorName,
                                onClick = { onProfileClick(group.service, group.userId) }
                            )
                        }
                    }

                    items(
                        count = group.posts.size,
                        key = { index -> "${group.service}:${group.userId}:${group.posts[index].id}" }
                    ) { index ->
                        val post = group.posts[index]
                        PostCard(
                            post = post,
                            onClick = { onPostClick(post) },
                            showFavCount = false,
                            uiSettingModel = uiSettingModel
                        )
                    }
                }

                appendStateItem(posts)
            }
        }

        PostsViewMode.GRID -> {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = uiSettingModel.postsSize.toDp()),
                verticalArrangement = Arrangement.spacedBy(uiSettingModel.postsSize.toArrangement()),
                horizontalArrangement = Arrangement.spacedBy(uiSettingModel.postsSize.toArrangement()),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                groups.forEach { group ->
                    item(
                        key = "header:${group.service}:${group.userId}",
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
                        Box(modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)) {
                            CreatorListItem(
                                dateMode = uiSettingModel.dateFormatMode,
                                service = group.service,
                                id = group.userId,
                                name = group.authorName,
                                onClick = { onProfileClick(group.service, group.userId) }
                            )
                        }
                    }

                    items(
                        count = group.posts.size,
                        key = { index -> "${group.service}:${group.userId}:${group.posts[index].id}" }
                    ) { index ->
                        val post = group.posts[index]
                        PostCard(
                            post = post,
                            onClick = { onPostClick(post) },
                            showFavCount = false,
                            uiSettingModel = uiSettingModel
                        )
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    AppendStateContent(posts = posts)
                }
            }
        }
    }
}

private fun buildAuthorGroups(
    items: List<PostDomain>,
    authorNamesByKey: Map<String, String>,
): List<AuthorGroup> {
    val groups = LinkedHashMap<String, AuthorGroup>()
    items.forEach { post ->
        val key = "${post.service}:${post.userId}"
        val group = groups.getOrPut(key) {
            AuthorGroup(
                service = post.service,
                userId = post.userId,
                authorName = authorNamesByKey[key] ?: "${post.service}/${post.userId}",
                posts = mutableListOf()
            )
        }
        group.posts += post
    }
    return groups.values.toList()
}

private fun androidx.compose.foundation.lazy.LazyListScope.appendStateItem(
    posts: LazyPagingItems<PostDomain>,
) {
    item {
        AppendStateContent(posts = posts)
    }
}

@Composable
private fun AppendStateContent(
    posts: LazyPagingItems<PostDomain>,
) {
    val errorMapper = LocalErrorMapper.current
    when (val append = posts.loadState.append) {
        is LoadState.Loading -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is LoadState.Error -> {
            val errorText = errorMapper.map(append.error).message
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = errorText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                    Button(onClick = { posts.retry() }) {
                        Text(text = stringResource(R.string.retry))
                    }
                }
            }
        }

        is LoadState.NotLoading -> Unit
    }
}

private data class AuthorGroup(
    val service: String,
    val userId: String,
    val authorName: String,
    val posts: MutableList<PostDomain>,
)

@Preview("PreviewFavoritePostsScreen")
@Composable
private fun PreviewFavoritePostsScreen() {
    KemonosPreviewScreen {
        FavoritePostsScreen(
            state = State(),
            onEvent = {},
            effect = emptyFlow(),
        )
    }
}
