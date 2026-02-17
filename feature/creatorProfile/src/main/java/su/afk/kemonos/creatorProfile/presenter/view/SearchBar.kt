package su.afk.kemonos.creatorProfile.presenter.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.components.posts.filter.PostMediaFilter
import su.afk.kemonos.ui.components.searchBar.PostsSearchBarWithMediaFilters

@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    mediaFilter: PostMediaFilter,
    onToggleHasVideo: () -> Unit,
    onToggleHasAttachments: () -> Unit,
    onToggleHasImages: () -> Unit,
    visible: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    AnimatedVisibility(visible = visible) {
        PostsSearchBarWithMediaFilters(
            query = searchText,
            onQueryChange = onSearchTextChange,
            mediaFilter = mediaFilter,
            onToggleHasVideo = onToggleHasVideo,
            onToggleHasAttachments = onToggleHasAttachments,
            onToggleHasImages = onToggleHasImages,
            label = stringResource(R.string.search),
            modifier = modifier,
            bottomPadding = 2,
            chipsTopPadding = 4,
            trailingIcon = {
                IconButton(onClick = {
                    onClose()
                }) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
                }
            },
            onSearch = { focusManager.clearFocus() }
        )
    }
}
