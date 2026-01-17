package su.afk.kemonos.creators.presenter.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import su.afk.kemonos.common.presenter.views.creator.grid.CreatorGridItem
import su.afk.kemonos.common.presenter.views.creator.list.CreatorListItem
import su.afk.kemonos.domain.models.Creators
import su.afk.kemonos.preferences.ui.CreatorViewMode

@Composable
fun CreatorsContentPaging(
    viewMode: CreatorViewMode,
    pagingItems: LazyPagingItems<Creators>,
    randomItems: List<Creators>,
    onCreatorClick: (Creators) -> Unit,
    listState: androidx.compose.foundation.lazy.LazyListState,
    gridState: androidx.compose.foundation.lazy.grid.LazyGridState,
) {
    when (viewMode) {
        CreatorViewMode.LIST -> {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(bottom = 8.dp),
            ) {
                randomCreatorsSection(items = randomItems, onCreatorClick = onCreatorClick)

                item {
                    HorizontalDivider(
                        Modifier.padding(top = 4.dp),
                        DividerDefaults.Thickness,
                        DividerDefaults.color
                    )
                }

                items(
                    count = pagingItems.itemCount,
                    key = pagingItems.itemKey { "all:${it.service}:${it.id}:${it.indexed}" }
                ) { index ->
                    val creator = pagingItems[index] ?: return@items
                    CreatorListItem(
                        service = creator.service,
                        id = creator.id,
                        name = creator.name,
                        favorited = creator.favorited,
                        onClick = { onCreatorClick(creator) }
                    )
                    HorizontalDivider()
                }
            }
        }

        CreatorViewMode.GRID -> {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(minSize = 150.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 8.dp),
            ) {
                randomCreatorsSection(items = randomItems, onCreatorClick = onCreatorClick)

                items(
                    count = pagingItems.itemCount,
                    key = pagingItems.itemKey { "all:${it.service}:${it.id}:${it.indexed}" }
                ) { index ->
                    val creator = pagingItems[index] ?: return@items
                    CreatorGridItem(
                        service = creator.service,
                        id = creator.id,
                        name = creator.name,
                        favorited = creator.favorited,
                        onClick = { onCreatorClick(creator) }
                    )
                }
            }
        }
    }
}