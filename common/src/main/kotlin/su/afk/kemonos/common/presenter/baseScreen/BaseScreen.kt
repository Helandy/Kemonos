package su.afk.kemonos.common.presenter.baseScreen

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
import su.afk.kemonos.common.R
import su.afk.kemonos.common.error.view.DefaultErrorContent
import su.afk.kemonos.domain.models.ErrorItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(
    modifier: Modifier = Modifier,
    /** content */
    contentAlignment: Alignment = Alignment.TopStart,
    contentModifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.background,

    /** layout */
    isScroll: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp),

    /** top */
    topBar: (@Composable (TopAppBarScrollBehavior?) -> Unit)? = null,
    topBarScroll: TopBarScroll = TopBarScroll.None,

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
    floatingActionButton: (@Composable () -> Unit)? = null,
    floatingActionButtonStart: (@Composable () -> Unit)? = null,
    floatingActionButtonBottomPadding: Dp = 0.dp,
    fabApplyScaffoldPadding: Boolean = true,

    applyScaffoldPadding: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scaffoldPadding = LocalAppScaffoldPadding.current
    val bottomInset = if (fabApplyScaffoldPadding) scaffoldPadding.calculateBottomPadding() else 0.dp

    val sb = when (topBarScroll) {
        TopBarScroll.None -> null
        TopBarScroll.EnterAlways -> TopAppBarDefaults.enterAlwaysScrollBehavior()
        TopBarScroll.ExitUntilCollapsed -> TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        TopBarScroll.Pinned -> TopAppBarDefaults.pinnedScrollBehavior()
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .let { if (sb != null) it.nestedScroll(sb.nestedScrollConnection) else it },
        color = containerColor
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (applyScaffoldPadding) Modifier.padding(scaffoldPadding) else Modifier)
            ) {
                if (topBar != null) topBar(sb)

                val base = contentModifier
                    .fillMaxSize()
                    .padding(contentPadding)

                val bodyModifier =
                    if (isScroll) base.verticalScroll(rememberScrollState()) else base

                when {
                    error != null -> (errorContent ?: { e, retry ->
                        DefaultErrorContent(
                            errorItem = e,
                            onRetry = retry,
                            onBack = onBack,
                        )
                    })(error, onRetry)
                    isLoading -> (loadingContent ?: { DefaultLoadingContent() })()
                    isEmpty -> (emptyContent ?: { DefaultEmptyContent() })()
                    else -> {
                        Box(
                            modifier = bodyModifier,
                            contentAlignment = contentAlignment
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                content = content
                            )
                        }
                    }
                }
            }

            if (floatingActionButtonStart != null || floatingActionButton != null) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(
                            start = 4.dp,
                            end = 4.dp,
                            bottom = bottomInset + floatingActionButtonBottomPadding
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box { floatingActionButtonStart?.invoke() }
                    Box { floatingActionButton?.invoke() }
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