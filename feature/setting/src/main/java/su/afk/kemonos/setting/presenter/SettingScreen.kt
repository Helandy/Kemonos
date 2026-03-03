package su.afk.kemonos.setting.presenter

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.deepLink.utils.openUrlInBrowser
import su.afk.kemonos.setting.BuildConfig
import su.afk.kemonos.setting.R
import su.afk.kemonos.setting.presenter.SettingState.Event
import su.afk.kemonos.setting.presenter.SettingState.State
import su.afk.kemonos.setting.presenter.screens.SettingsPreview
import su.afk.kemonos.setting.presenter.screens.SettingsScreenScaffold
import su.afk.kemonos.setting.presenter.screens.previewSettingState
import su.afk.kemonos.setting.presenter.view.language.openAppDeepLinksSettingsSafely
import su.afk.kemonos.setting.presenter.view.language.openAppLanguageSettingsSafely
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingScreen(
    state: State,
    onEvent: (Event) -> Unit,
    effect: Flow<SettingState.Effect>
) {
    val context = LocalContext.current

    SettingsScreenScaffold(
        title = stringResource(R.string.setting),
        onBack = { onEvent(Event.Back) },
        isLoading = state.loading,
        contentModifier = Modifier.padding(horizontal = 12.dp),
        topBarScroll = TopBarScroll.ExitUntilCollapsed,
    ) {
        val hubItems = listOf(
            SettingHubEntry(
                icon = Icons.Outlined.Settings,
                title = stringResource(R.string.settings_ui_title),
                subtitle = stringResource(R.string.settings_hub_ui_subtitle),
                onClick = { onEvent(Event.OpenUiSettings) },
            ),
            SettingHubEntry(
                icon = Icons.Outlined.Info,
                title = stringResource(R.string.settings_translate_title),
                subtitle = stringResource(R.string.settings_hub_translate_subtitle),
                onClick = { onEvent(Event.OpenTranslateSettings) },
            ),
            SettingHubEntry(
                icon = Icons.Outlined.Link,
                title = stringResource(R.string.settings_hub_network_title),
                subtitle = stringResource(R.string.settings_hub_network_subtitle),
                onClick = { onEvent(Event.OpenNetworkSettings) },
            ),
            SettingHubEntry(
                icon = Icons.Outlined.FolderOpen,
                title = stringResource(R.string.settings_hub_database_title),
                subtitle = stringResource(R.string.settings_hub_database_subtitle),
                onClick = { onEvent(Event.OpenDatabaseSettings) },
            ),
            SettingHubEntry(
                icon = Icons.Outlined.Download,
                title = stringResource(R.string.settings_downloads_title),
                subtitle = stringResource(R.string.settings_hub_downloads_subtitle),
                onClick = { onEvent(Event.OpenDownloadSettings) },
            ),
            SettingHubEntry(
                icon = Icons.Outlined.Language,
                title = stringResource(R.string.settings_ui_app_language_title),
                subtitle = stringResource(R.string.settings_ui_app_language_subtitle),
                onClick = { context.openAppLanguageSettingsSafely() },
            ),
            SettingHubEntry(
                icon = Icons.AutoMirrored.Outlined.OpenInNew,
                title = stringResource(R.string.settings_ui_deep_links_title),
                subtitle = stringResource(R.string.settings_ui_deep_links_subtitle),
                onClick = { context.openAppDeepLinksSettingsSafely() },
            ),
        )

        Text(
            text = stringResource(R.string.settings_hub_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            hubItems.forEach { item ->
                SettingsHubItem(
                    icon = item.icon,
                    title = item.title,
                    subtitle = item.subtitle,
                    onClick = item.onClick,
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.settings_github_ideas),
        )

        FilledTonalButton(
            onClick = { openUrlInBrowser(context, "https://github.com/Helandy/Kemonos") },
            modifier = Modifier.fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Text(
                text = stringResource(R.string.settings_hub_version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

private data class SettingHubEntry(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit,
)

@Composable
private fun SettingsHubItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.small,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview("PreviewSettingScreen")
@Composable
private fun PreviewSettingScreen() {
    SettingsPreview {
        SettingScreen(
            state = previewSettingState(),
            onEvent = {},
            effect = emptyFlow(),
        )
    }
}
