package su.afk.kemonos.common.view.creator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import su.afk.kemonos.common.R
import su.afk.kemonos.common.view.creator.section.CreatorsSectionHeader
import su.afk.kemonos.common.view.creator.section.randomCreatorsSection
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.preferences.ui.CreatorViewMode
import su.afk.kemonos.preferences.ui.DateFormatMode

@Composable
fun CreatorsContentPaging(
    dateMode: DateFormatMode,
    viewMode: CreatorViewMode,
    pagingItems: LazyPagingItems<FavoriteArtist>,
    randomItems: List<FavoriteArtist>,
    onCreatorClick: (FavoriteArtist) -> Unit,
    listState: LazyListState,
    gridState: LazyGridState,
    updatedProvider: ((FavoriteArtist) -> String?)? = null,
    isFreshProvider: ((FavoriteArtist) -> Boolean)? = null,
    expanded: Boolean? = null,
    onClickRandomHeader: (() -> Unit)? = null,
) {
    fun updatedFor(item: FavoriteArtist): String? = updatedProvider?.invoke(item)
    fun freshFor(item: FavoriteArtist): Boolean = isFreshProvider?.invoke(item) ?: false


    when (viewMode) {
        CreatorViewMode.LIST -> {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(bottom = 8.dp),
            ) {
                randomCreatorsSection(
                    items = randomItems,
                    onCreatorClick = onCreatorClick,
                    dateMode = dateMode,
                    expanded = expanded,
                    onClickRandomHeader = onClickRandomHeader,
                )

                item(key = "all_title") {
                    if (randomItems.isNotEmpty()) {
                        CreatorsSectionHeader(
                            title = stringResource(R.string.creators_title),
                            showTopDivider = false,
                            showBottomDivider = true
                        )
                    }
                }

                items(
                    count = pagingItems.itemCount,
                    key = pagingItems.itemKey { "all:${it.service}:${it.id}:${it.relationId}:${it.favedSeq}" }
                ) { index ->
                    val creator = pagingItems[index] ?: return@items
                    CreatorListItem(
                        dateMode = dateMode,
                        service = creator.service,
                        id = creator.id,
                        name = creator.name,
                        favorited = creator.favorited,
                        onClick = { onCreatorClick(creator) },
                        updated = updatedFor(creator),
                        isFresh = freshFor(creator),
                    )
                    HorizontalDivider()
                }
            }
        }

        CreatorViewMode.GRID -> {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(minSize = 160.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 8.dp),
            ) {
                randomCreatorsSection(
                    items = randomItems,
                    onCreatorClick = onCreatorClick,
                    dateMode = dateMode,
                    expanded = expanded,
                    onClickRandomHeader = onClickRandomHeader,
                )

                item(
                    key = "all_title",
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    if (randomItems.isNotEmpty()) {
                        CreatorsSectionHeader(
                            title = stringResource(R.string.creators_title),
                            showTopDivider = false,
                            showBottomDivider = true
                        )
                    }
                }

                items(
                    count = pagingItems.itemCount,
                    key = pagingItems.itemKey { "all:${it.service}:${it.id}:${it.relationId}:${it.favedSeq}" }
                ) { index ->
                    val creator = pagingItems[index] ?: return@items
                    CreatorGridItem(
                        dateMode = dateMode,
                        service = creator.service,
                        id = creator.id,
                        name = creator.name,
                        favorited = creator.favorited,
                        onClick = { onCreatorClick(creator) },
                        updated = updatedFor(creator),
                        isFresh = freshFor(creator),
                    )
                }
            }
        }
    }
}