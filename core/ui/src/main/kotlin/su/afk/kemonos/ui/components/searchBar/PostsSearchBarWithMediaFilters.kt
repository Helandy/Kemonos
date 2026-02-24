package su.afk.kemonos.ui.components.searchBar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.components.posts.filter.PostMediaFilter
import su.afk.kemonos.ui.components.posts.filter.PostMediaFilterChips

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsSearchBarWithMediaFilters(
    query: String,
    onQueryChange: (String) -> Unit,
    mediaFilter: PostMediaFilter,
    onToggleHasVideo: () -> Unit,
    onToggleHasAttachments: () -> Unit,
    onToggleHasImages: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    bottomPadding: Int = 4,
    chipsTopPadding: Int = 8,
    showMediaFiltersInfoTooltip: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    onSearch: () -> Unit = {},
) {
    val tooltipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = bottomPadding.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text(label) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = trailingIcon,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSearch() }
            )
        )

        if (showMediaFiltersInfoTooltip) {
            Row(
                modifier = Modifier.padding(top = chipsTopPadding.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TooltipBox(
                    state = tooltipState,
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip {
                            Text(stringResource(R.string.posts_filter_local_info_tooltip))
                        }
                    }
                ) {
                    IconButton(
                        modifier = Modifier.size(36.dp),
                        onClick = { scope.launch { tooltipState.show() } }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = stringResource(R.string.info),
                        )
                    }
                }

                Spacer(Modifier.width(4.dp))

                PostMediaFilterChips(
                    filter = mediaFilter,
                    onToggleHasVideo = onToggleHasVideo,
                    onToggleHasAttachments = onToggleHasAttachments,
                    onToggleHasImages = onToggleHasImages,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            PostMediaFilterChips(
                filter = mediaFilter,
                onToggleHasVideo = onToggleHasVideo,
                onToggleHasAttachments = onToggleHasAttachments,
                onToggleHasImages = onToggleHasImages,
                modifier = Modifier.padding(top = chipsTopPadding.dp),
            )
        }
    }
}
