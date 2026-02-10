package su.afk.kemonos.common.components.searchBar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.components.posts.filter.PostMediaFilter
import su.afk.kemonos.common.components.posts.filter.PostMediaFilterChips

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
    trailingIcon: @Composable (() -> Unit)? = null,
    onSearch: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text(label) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = trailingIcon,
            keyboardActions = KeyboardActions(
                onSearch = { onSearch() }
            )
        )
        PostMediaFilterChips(
            filter = mediaFilter,
            onToggleHasVideo = onToggleHasVideo,
            onToggleHasAttachments = onToggleHasAttachments,
            onToggleHasImages = onToggleHasImages,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
