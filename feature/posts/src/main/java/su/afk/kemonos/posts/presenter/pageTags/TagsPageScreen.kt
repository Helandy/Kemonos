package su.afk.kemonos.posts.presenter.pageTags

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import su.afk.kemonos.common.R
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.baseScreen.StandardTopBar
import su.afk.kemonos.common.presenter.changeSite.SiteToggleFab
import su.afk.kemonos.posts.api.tags.Tags

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TagsPageScreen(
    viewModel: TagsPageViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val site by viewModel.site.collectAsStateWithLifecycle()
    val siteSwitching by viewModel.siteSwitching.collectAsStateWithLifecycle()

    val isPageLoading = state.loading || siteSwitching

    val isBusy = isPageLoading

    val chunkedTags = remember(state.tags) {
        state.tags.sortedByDescending { it.count }.chunked(50)
    }

    BaseScreen(
        contentPadding = PaddingValues(horizontal = 8.dp),
        applyScaffoldPadding = false,
        isScroll = false,
        topBar = { scrollBehavior ->
            StandardTopBar(
                scrollBehavior = scrollBehavior,
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    label = { Text(stringResource(R.string.search)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                )
            }
        },
        floatingActionButtonStart = {
            SiteToggleFab(
                enable = !isBusy,
                selectedSite = site,
                onToggleSite = viewModel::switchSite,
            )
        },
        fabApplyScaffoldPadding = false,
        floatingActionButtonBottomPadding = 12.dp,
        isLoading = isBusy,
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(chunkedTags) { rowTags ->
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    rowTags.forEach { tag ->
                        key(tag.hashCode()) {
                            TagChip(
                                tag = tag,
                                onClick = {
                                    viewModel.navigateToSelectTag(tag = tag.tags)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun TagChip(
    tag: Tags,
    onClick: () -> Unit
) {
    Text(
        text = "#${tag.tags} (${tag.count})",
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}