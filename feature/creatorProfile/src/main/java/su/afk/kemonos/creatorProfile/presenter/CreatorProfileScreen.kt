package su.afk.kemonos.creatorProfile.presenter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import su.afk.kemonos.common.util.toUiDateTime
import su.afk.kemonos.common.utilsUI.KemonosPreviewScreen
import su.afk.kemonos.creatorProfile.presenter.CreatorProfileState.*
import su.afk.kemonos.creatorProfile.presenter.CreatorProfileState.State
import su.afk.kemonos.creatorProfile.presenter.model.ProfileTab
import su.afk.kemonos.creatorProfile.presenter.view.*
import su.afk.kemonos.creatorProfile.presenter.view.discordProfile.DiscordProfilePlaceholder
import su.afk.kemonos.deepLink.utils.openUrlPreferChrome
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.common.R as CommonR

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
            var expanded by remember { mutableStateOf(false) }
            val extrasVisible by remember(scrollBehavior) {
                derivedStateOf {
                    scrollBehavior?.state?.heightOffset?.let { it >= -1f } ?: true
                }
            }
            val isAtStartOfPage by remember(scrollBehavior) {
                derivedStateOf {
                    scrollBehavior?.state?.let { appBarState ->
                        kotlin.math.abs(appBarState.heightOffset) < 0.5f &&
                                kotlin.math.abs(appBarState.contentOffset) < 0.5f
                    } ?: true
                }
            }

            Column {
                CreatorCenterBackTopBar(
                    title = profile?.name.orEmpty(),
                    onBack = { onEvent(Event.Back) },
                    scrollBehavior = scrollBehavior,
                    actions = {
                        if (profile == null) return@CreatorCenterBackTopBar

                        IconButton(onClick = { onEvent(Event.ToggleSearch) }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(CommonR.string.search),
                            )
                        }

                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null,
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(CommonR.string.share)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    expanded = false
                                    onEvent(Event.CopyProfileLink)
                                }
                            )

                            platformUrl?.let { link ->
                                DropdownMenuItem(
                                    text = { Text(stringResource(CommonR.string.open_platform_profile)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.OpenInBrowser,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        expanded = false
                                        onEvent(Event.OpenCreatorPlatformLink(link))
                                    }
                                )
                            }

                            profile.updated?.let { upd ->
                                DropdownMenuItem(
                                    text = { Text(upd.toUiDateTime(state.uiSettingModel.dateFormatMode)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = null
                                        )
                                    },
                                    enabled = false,
                                    onClick = {}
                                )
                            }
                        }
                    }
                )

                AnimatedVisibility(visible = extrasVisible) {
                    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                        if (profile == null) return@AnimatedVisibility

                        AnimatedVisibility(visible = isAtStartOfPage) {
                            CreatorHeader(
                                dateMode = state.uiSettingModel.dateFormatMode,
                                service = profile.service,
                                creatorId = profile.id,
                                creatorName = profile.name,
                                updated = profile.updated,
                                onBackClick = null,
                                showSearchButton = false,
                                showInfoButton = false,
                                onSearchClick = {},
                                onOpenPlatformClick = null,
                                onShareClick = null,
                                onClickHeader = null,
                            )
                        }

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
                    }
                }
            }
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
            onTabSelected = { tab ->
                onEvent(Event.TabChanged(tab))
            },
            currentTag = state.currentTag,
            onTagClear = { onEvent(Event.ClearTag) }
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

private fun buildCreatorPlatformUrl(service: String, publicId: String?, id: String): String? {
    val slug = publicId?.trim()?.removePrefix("@")?.ifBlank { null } ?: id.trim().ifBlank { return null }

    return when (service.lowercase()) {
        "patreon" -> "https://www.patreon.com/$slug"
        "fanbox" -> "https://www.fanbox.cc/@$slug"
        "onlyfans" -> "https://onlyfans.com/$slug"
        "fansly" -> "https://fansly.com/$slug"
        "candfans" -> "https://candfans.jp/$slug"
        "boosty" -> "https://boosty.to/$slug"
        "fantia" -> "https://fantia.jp/fanclubs/$slug"
        "gumroad" -> "https://$slug.gumroad.com"
        "subscribestar", "subscriblestar" -> "https://subscribestar.adult/$slug"
        "dlsite", "dlslite" -> "https://www.dlsite.com/home/circle/profile/=/maker_id/$slug.html?locale=en_US"
        else -> null
    }
}
