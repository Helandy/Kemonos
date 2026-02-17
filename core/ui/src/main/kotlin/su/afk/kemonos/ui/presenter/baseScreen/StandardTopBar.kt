package su.afk.kemonos.ui.presenter.baseScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardTopBar(
    scrollBehavior: TopAppBarScrollBehavior?,
    content: @Composable ColumnScope.() -> Unit,
    topBarWindowInsets: WindowInsets
) {
    val compensateEnd = 16.dp

    TopAppBar(
        windowInsets = topBarWindowInsets,
        scrollBehavior = scrollBehavior,
        title = {
            Column(
                Modifier.fillMaxWidth()
                    .padding(end = compensateEnd) // чтобы визуально было ровно по правому краю
            ) {
                content()
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}
