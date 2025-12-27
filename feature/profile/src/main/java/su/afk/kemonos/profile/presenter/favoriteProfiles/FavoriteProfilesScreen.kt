package su.afk.kemonos.profile.presenter.favoriteProfiles

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.baseScreen.StandardTopBar
import su.afk.kemonos.common.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.common.presenter.views.imageLoader.AsyncImageWithStatus
import su.afk.kemonos.common.presenter.views.searchBar.SearchBarNew
import su.afk.kemonos.common.util.getColorForFavorites
import su.afk.kemonos.common.util.selectDomain.getImageBaseUrlByService
import su.afk.kemonos.profile.api.model.FavoriteArtist
import su.afk.kemonos.profile.presenter.favoriteProfiles.views.favoriteProfilesSortOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoriteProfilesScreen(viewModel: FavoriteProfilesViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sortOptions = favoriteProfilesSortOptions()

    BaseScreen(
        modifier = Modifier.padding(start = 4.dp),
        contentModifier = Modifier.padding(horizontal = 8.dp),
        isScroll = false,
        floatingActionButtonBottomPadding = 12.dp,
        topBarScroll = TopBarScroll.EnterAlways,
        topBar = { scrollBehavior ->
            StandardTopBar(
                scrollBehavior = scrollBehavior,
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
            ) {
                SearchBarNew(
                    query = state.searchQuery,
                    onQueryChange = viewModel::updateSearch,
                    services = viewModel.getServices(),
                    selectedService = state.selectedService,
                    onServiceSelect = viewModel::setService,
                    sortOptions = sortOptions,
                    selectedSort = state.sortedType,
                    onSortMethodSelect = viewModel::setSortType,
                    isAscending = state.sortAscending,
                    onToggleAscending = viewModel::toggleSortOrder,
                )
            }
        },
        isLoading = state.loading,
        isEmpty = state.searchCreators.isEmpty() && state.searchQuery.length >= 2,
        onRetry = { viewModel.load() }
    ) {
        LazyColumn {
            item {
                HorizontalDivider(
                    Modifier.padding(top = 4.dp),
                    DividerDefaults.Thickness,
                    DividerDefaults.color
                )
            }

            items(
                items = state.searchCreators,
                key = { "${it.service}:${it.id}:${it.indexed}" }
            ) { creator ->
                CreatorItem(creator) { viewModel.onCreatorClick(creator) }
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun CreatorItem(creator: FavoriteArtist, onClick: () -> Unit) {
    val avatarSize = LocalWindowInfo.current.containerSize.width * 0.15f

    val imgBaseUrl = remember(creator.service) {
        getImageBaseUrlByService(creator.service)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        AsyncImageWithStatus(
            model = "$imgBaseUrl/banners/${creator.service}/${creator.id}",
            contentDescription = "Banner for ${creator.name}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(4.dp))
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f))
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImageWithStatus(
                model = "$imgBaseUrl/icons/${creator.service}/${creator.id}",
                contentDescription = creator.name,
                modifier = Modifier
                    .size(avatarSize.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = creator.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .border(
                            2.dp,
                            getColorForFavorites(creator.service),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = creator.service,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = getColorForFavorites(creator.service)
                    )
                }
            }
        }
    }
}

