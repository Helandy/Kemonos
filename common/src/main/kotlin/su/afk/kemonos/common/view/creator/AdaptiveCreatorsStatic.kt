package su.afk.kemonos.common.view.creator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.CreatorViewMode

@Composable
fun <T : Any> AdaptiveCreatorsStatic(
    viewMode: CreatorViewMode,
    items: List<T>,
    key: (T) -> Any,
    modifier: Modifier = Modifier,
    // list
    showTopDivider: Boolean = true,
    listItem: @Composable (T) -> Unit,
    // grid
    minCellSize: Dp = 160.dp,
    gridSpacing: Dp = 6.dp,
    gridContentPadding: PaddingValues = PaddingValues(top = 6.dp, bottom = 12.dp),
    gridItem: @Composable (T) -> Unit,
) {
    if (viewMode == CreatorViewMode.LIST) {
        LazyColumn(modifier = modifier) {
            if (showTopDivider) {
                item {
                    HorizontalDivider(
                        Modifier.padding(top = 4.dp),
                        DividerDefaults.Thickness,
                        DividerDefaults.color
                    )
                }
            }

            items(items = items, key = key) { item ->
                listItem(item)
                HorizontalDivider()
            }
        }
    } else {
        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Adaptive(minSize = minCellSize),
            verticalArrangement = Arrangement.spacedBy(gridSpacing),
            horizontalArrangement = Arrangement.spacedBy(gridSpacing),
            contentPadding = gridContentPadding,
        ) {
            items(items = items, key = key) { item ->
                gridItem(item)
            }
        }
    }
}