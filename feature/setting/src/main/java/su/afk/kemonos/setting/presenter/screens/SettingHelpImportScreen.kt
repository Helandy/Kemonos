package su.afk.kemonos.setting.presenter.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.deepLink.utils.openUrlInBrowser
import su.afk.kemonos.setting.R
import su.afk.kemonos.setting.presenter.SettingState.Event
import su.afk.kemonos.setting.presenter.SettingState.State
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

private const val KEMONO_IMPORTER_URL = "https://kemono.cr/importer"
private const val COOMER_IMPORTER_URL = "https://coomer.st/importer"

@Composable
internal fun SettingHelpImportScreen(
    state: State,
    onEvent: (Event) -> Unit,
) {
    val context = LocalContext.current

    SettingsScreenScaffold(
        title = stringResource(R.string.settings_help_import_title),
        onBack = { onEvent(Event.Back) },
        isLoading = state.loading,
        contentModifier = Modifier.padding(horizontal = 8.dp),
        topBarScroll = TopBarScroll.Pinned,
    ) {
        Text(
            text = stringResource(R.string.settings_help_import_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(14.dp))

        ImportHelpCard(
            text = stringResource(R.string.settings_help_import_kemono_text),
            buttonText = stringResource(R.string.settings_help_import_open_button),
            onClick = { openUrlInBrowser(context, KEMONO_IMPORTER_URL) },
        )

        Spacer(Modifier.height(10.dp))

        ImportHelpCard(
            text = stringResource(R.string.settings_help_import_coomer_text),
            buttonText = stringResource(R.string.settings_help_import_open_button),
            onClick = { openUrlInBrowser(context, COOMER_IMPORTER_URL) },
        )
    }
}

@Composable
private fun ImportHelpCard(
    text: String,
    buttonText: String,
    onClick: () -> Unit,
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
            )

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = buttonText)
            }
        }
    }
}

@Preview(name = "Setting Help Import", showBackground = true)
@Composable
private fun PreviewSettingHelpImportScreen() {
    SettingsPreview {
        SettingHelpImportScreen(
            state = previewSettingState(),
            onEvent = {},
        )
    }
}
