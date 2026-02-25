package su.afk.kemonos.ui.presenter.baseScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.error.error.view.DefaultErrorContent
import su.afk.kemonos.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(
    /** content */
    contentAlignment: Alignment = Alignment.TopStart,
    contentModifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.background,

    /** layout */
    isScroll: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp),

    /** top */
    topBar: (@Composable ColumnScope.() -> Unit)? = null,
    customTopBar: (@Composable (TopAppBarScrollBehavior?) -> Unit)? = null,
    topBarScroll: TopBarScroll = TopBarScroll.None,
    topBarWindowInsets: WindowInsets = TopAppBarDefaults.windowInsets,

    /** state */
    error: ErrorItem? = null,
    onRetry: () -> Unit = {},
    onBack: (() -> Unit)? = null,
    isLoading: Boolean = false,
    isEmpty: Boolean = false,

    errorContent: (@Composable (ErrorItem, onRetry: () -> Unit) -> Unit)? = null,
    loadingContent: (@Composable () -> Unit)? = null,
    emptyContent: (@Composable () -> Unit)? = null,

    /** floating */
    floatingActionButtonEnd: (@Composable () -> Unit)? = null,
    floatingActionButtonStart: (@Composable () -> Unit)? = null,
    floatingActionButtonBottomPadding: Dp = 0.dp,

    content: @Composable ColumnScope.() -> Unit,
) {
    val topAppBarState = rememberTopAppBarState()

    val scrollBehavior = when (topBarScroll) {
        TopBarScroll.None -> null
        TopBarScroll.EnterAlways ->
            TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

        TopBarScroll.ExitUntilCollapsed ->
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

        TopBarScroll.Pinned ->
            TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing.only(
            WindowInsetsSides.Top + WindowInsetsSides.Horizontal
        ),
        modifier = Modifier
            .fillMaxSize()
            .let {
                if (scrollBehavior != null)
                    it.nestedScroll(scrollBehavior.nestedScrollConnection)
                else it
            },
        topBar = {
            if (customTopBar != null) {
                customTopBar(scrollBehavior)
            } else {
                topBar?.let { slot ->
                    StandardTopBar(
                        content = { slot() },
                        scrollBehavior = scrollBehavior,
                        topBarWindowInsets = topBarWindowInsets
                    )
                }
            }
        },

        floatingActionButton = {
            if (floatingActionButtonStart != null || floatingActionButtonEnd != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 4.dp,
                            end = 4.dp,
                            bottom = floatingActionButtonBottomPadding
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Box { floatingActionButtonStart?.invoke() }
                    Box { floatingActionButtonEnd?.invoke() }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->
        val base = contentModifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(contentPadding)

        val bodyModifier = if (isScroll) base.verticalScroll(rememberScrollState()) else base

        when {
            error != null -> Box(modifier = base) {
                (errorContent ?: { e, retry ->
                    DefaultErrorContent(
                        errorItem = e,
                        onRetry = retry,
                        onBack = onBack,
                    )
                })(error, onRetry)
            }

            isLoading -> Box(modifier = base) {
                (loadingContent ?: { DefaultLoadingContent() })()
            }

            isEmpty -> Box(
                modifier = base,
                contentAlignment = Alignment.Center
            ) {
                (emptyContent ?: { DefaultEmptyContent() })()
            }

            else -> {
                Box(
                    modifier = bodyModifier,
                    contentAlignment = contentAlignment
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        content = content
                    )
                }
            }
        }
    }
}

@Composable
fun DefaultLoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun DefaultEmptyContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.empty_screen),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/**
 * topBar не реагирует на скролл
 * pinned (обычно не “уезжает”)
 * появляется сверху при прокрутке
 * */
@OptIn(ExperimentalMaterial3Api::class)
sealed interface TopBarScroll {
    data object None : TopBarScroll
    data object Pinned : TopBarScroll
    data object EnterAlways : TopBarScroll
    data object ExitUntilCollapsed : TopBarScroll
}
