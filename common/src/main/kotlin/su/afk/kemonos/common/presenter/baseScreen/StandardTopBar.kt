package su.afk.kemonos.common.presenter.baseScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardTopBar(
    scrollBehavior: TopAppBarScrollBehavior?,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(top = 0.dp, end = 16.dp),
    divider: Boolean = false,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        CenterAlignedTopAppBar(
            windowInsets = windowInsets,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
            ),
            navigationIcon = {},
            actions = {},
            scrollBehavior = scrollBehavior,
            title = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(contentPadding),
                ) {
                    content()

                    if (divider) {
                        HorizontalDivider(
                            Modifier.padding(top = 8.dp),
                            DividerDefaults.Thickness,
                            DividerDefaults.color
                        )
                    }
                }
            }
        )
    }
}
