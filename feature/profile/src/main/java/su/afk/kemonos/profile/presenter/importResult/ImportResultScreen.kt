package su.afk.kemonos.profile.presenter.importResult

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.presenter.importResult.ImportResultState.Event
import su.afk.kemonos.profile.presenter.importResult.ImportResultState.State
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.CenterBackTopBar
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ImportResultScreen(
    state: State,
    onEvent: (Event) -> Unit,
) {
    val payload = state.payload
    val title = payload?.title ?: stringResource(R.string.profile_import_result_default_title)

    BaseScreen(
        isScroll = false,
        contentPadding = PaddingValues(horizontal = 12.dp),
        topBarScroll = TopBarScroll.EnterAlways,
        customTopBar = { scrollBehavior ->
            CenterBackTopBar(
                title = title,
                onBack = { onEvent(Event.Back) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) {
        if (payload == null) {
            Text(
                text = stringResource(R.string.profile_import_result_empty),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp),
            )
            return@BaseScreen
        }

        Text(
            text = payload.summary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp),
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (payload.items.isEmpty()) {
            Text(
                text = stringResource(R.string.profile_import_result_empty),
                style = MaterialTheme.typography.bodyLarge,
            )
            return@BaseScreen
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(
                items = payload.items,
                key = { item -> "${item.rowNumber}:${item.target}:${item.status}" }
            ) { item ->
                ImportResultItemCard(item = item)
            }
        }
    }
}

@Composable
private fun ImportResultItemCard(item: ImportResultItem) {
    val statusLabelRes = when (item.status) {
        ImportResultStatus.SUCCESS -> R.string.profile_import_result_status_success
        ImportResultStatus.FAILED -> R.string.profile_import_result_status_failed
        ImportResultStatus.SKIPPED -> R.string.profile_import_result_status_skipped
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "#${item.rowNumber} - ${stringResource(statusLabelRes)}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = item.target,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (item.reason.isNotBlank()) {
                Text(
                    text = item.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
