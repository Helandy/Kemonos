package su.afk.kemonos.posts.presenter.pager.views

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import su.afk.kemonos.posts.presenter.pager.model.ALL_POSTS_PAGES
import su.afk.kemonos.posts.presenter.pager.model.PostsPage
import kotlin.math.ceil

@Composable
internal fun PagerTabs(
    currentPage: PostsPage,
    onTabSelected: (PostsPage) -> Unit,
) {
    val pages = remember { ALL_POSTS_PAGES }

    val selectedIndex = pages.indexOf(currentPage).let { if (it >= 0) it else 0 }
    val safeCurrentPage = pages.getOrNull(selectedIndex) ?: pages.first()

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val density = LocalDensity.current
        val maxWidthPx = with(density) { maxWidth.toPx() }

        val textStyle: TextStyle = MaterialTheme.typography.bodyMedium

        // Оценим общую ширину табов:
        // символы * среднюю ширину символа + паддинги таба + edgePadding*2
        val avgCharPx = with(density) { (textStyle.fontSize.value * 0.55f).spToPxFallback() }
        val tabHorizontalPaddingPx = with(density) { 24.dp.toPx() } // примерно как у Tab
        val edgePaddingPx = with(density) { 8.dp.toPx() } * 2

        val estimatedTabsWidthPx = pages.sumOf { page ->
            val title = stringResource(page.titleRes)
            val textPx = ceil(title.length * avgCharPx).toInt().toFloat()
            (textPx + tabHorizontalPaddingPx * 2).toDouble()
        }.toFloat() + edgePaddingPx

        val fits = estimatedTabsWidthPx <= maxWidthPx

        if (fits) {
            // Центрируется/равномерно распределяется по ширине
            TabRow(
                selectedTabIndex = selectedIndex,
                modifier = Modifier.fillMaxWidth(),
            ) {
                pages.forEach { page ->
                    Tab(
                        selected = safeCurrentPage == page,
                        onClick = { onTabSelected(page) },
                        text = {
                            Text(
                                text = stringResource(page.titleRes),
                                style = textStyle,
                                maxLines = 1,
                            )
                        }
                    )
                }
            }
        } else {
            // Скроллится при переполнении
            ScrollableTabRow(
                selectedTabIndex = selectedIndex,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 8.dp,
            ) {
                pages.forEach { page ->
                    Tab(
                        selected = safeCurrentPage == page,
                        onClick = { onTabSelected(page) },
                        text = {
                            Text(
                                text = stringResource(page.titleRes),
                                style = textStyle,
                                maxLines = 1,
                            )
                        }
                    )
                }
            }
        }
    }
}

/**
 * Небольшой хак: если fontSize в sp, то через density можно получить px.
 * Для простоты: sp ~= dp для расчетов, но точность здесь не критична.
 */
private fun Float.spToPxFallback(): Float = this