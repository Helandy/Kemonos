package su.afk.kemonos.common.paging

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import su.afk.kemonos.common.utilsUI.KemonosPreviewScreen
import su.afk.kemonos.domain.models.ErrorItem

/**
 * Paging loadger + error retry button
 * */
@Composable
internal fun PagingAppendStateItem(
    loadState: LoadState,
    onRetry: () -> Unit,
    parseError: (Throwable) -> ErrorItem,
) {
    when (loadState) {
        is LoadState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is LoadState.Error -> {
            PagingAppendErrorFooter(
                errorItem = parseError(loadState.error),
                onRetry = onRetry
            )
        }

        is LoadState.NotLoading -> Unit
    }
}

@Preview("PagingAppend • Loading")
@Composable
private fun PreviewPagingAppendLoading() {
    KemonosPreviewScreen {
        PagingAppendStateItem(
            loadState = LoadState.Loading,
            onRetry = {},
            parseError = { error ->
                ErrorItem(
                    title = "Error",
                    message = error.message ?: "Unknown error"
                )
            }
        )
    }
}

@Preview("PagingAppend • Error")
@Composable
private fun PreviewPagingAppendError() {
    KemonosPreviewScreen {
        PagingAppendStateItem(
            loadState = LoadState.Error(RuntimeException("Network error")),
            onRetry = {},
            parseError = { error ->
                ErrorItem(
                    title = "Error",
                    message = error.message ?: "Unknown error"
                )
            }
        )
    }
}