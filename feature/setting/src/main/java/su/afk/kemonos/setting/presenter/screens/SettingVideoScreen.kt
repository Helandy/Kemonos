package su.afk.kemonos.setting.presenter.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.setting.R
import su.afk.kemonos.setting.presenter.SettingState.Event
import su.afk.kemonos.setting.presenter.SettingState.State
import su.afk.kemonos.setting.presenter.view.SwitchRow
import su.afk.kemonos.setting.presenter.view.common.SectionSpacer
import su.afk.kemonos.setting.presenter.view.common.SettingsSectionTitle
import su.afk.kemonos.setting.presenter.view.viewSettings.VideoPreviewAspectRatioRow
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

private const val VIDEO_PREVIEW_SERVER_GITHUB_URL = "https://github.com/Helandy/KemonosVideoMetaApi"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingVideoScreen(
    state: State,
    onEvent: (Event) -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    SettingsScreenScaffold(
        title = stringResource(R.string.settings_hub_video_title),
        onBack = { onEvent(Event.Back) },
        isLoading = state.loading,
        contentModifier = Modifier.padding(horizontal = 8.dp),
        topBarScroll = TopBarScroll.Pinned,
    ) {
        SectionSpacer()
        SettingsSectionTitle(text = stringResource(R.string.settings_video_playback_title))
        Spacer(Modifier.height(6.dp))

        SwitchRow(
            title = stringResource(R.string.settings_show_preview_video_title),
            checked = state.uiSettingModel.showPreviewVideo,
            onCheckedChange = { onEvent(Event.ChangeViewSetting.ShowPreviewVideo(it)) },
        )
        VideoPreviewAspectRatioRow(
            value = state.uiSettingModel.videoPreviewAspectRatio,
            onChange = { onEvent(Event.ChangeViewSetting.VideoPreviewAspectRatioChanged(it)) },
        )

        SwitchRow(
            title = stringResource(R.string.settings_autoplay_community_video_title),
            subtitle = stringResource(R.string.settings_autoplay_community_video_subtitle),
            checked = state.uiSettingModel.autoplayCommunityVideo,
            onCheckedChange = { onEvent(Event.ChangeViewSetting.AutoplayCommunityVideo(it)) },
        )

        Spacer(Modifier.height(18.dp))
        SettingsSectionTitle(text = stringResource(R.string.settings_network_video_preview_title))
        Spacer(Modifier.height(6.dp))

        SwitchRow(
            title = stringResource(R.string.settings_use_external_metadata_title),
            checked = state.uiSettingModel.useExternalMetaData,
            onCheckedChange = { onEvent(Event.ChangeViewSetting.UseExternalMetaData(it)) },
        )

        Spacer(Modifier.height(10.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(
                        R.string.settings_video_preview_server_current,
                        state.uiSettingModel.videoPreviewServerUrl
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.inputVideoPreviewServerDomain,
            onValueChange = {
                onEvent(Event.ApiSetting.InputVideoPreviewServerDomainChanged(it))
            },
            singleLine = true,
            label = { Text(stringResource(R.string.settings_video_preview_server_title)) },
            prefix = { Text("https://") },
        )

        Spacer(Modifier.height(10.dp))

        TextButton(
            onClick = { uriHandler.openUri(VIDEO_PREVIEW_SERVER_GITHUB_URL) },
        ) {
            Text(
                text = stringResource(R.string.settings_video_preview_server_hint),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { onEvent(Event.ApiSetting.SaveUrls) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSaving
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(10.dp))
            }
            Text(stringResource(su.afk.kemonos.ui.R.string.save))
        }

        if (state.saveSuccess) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(su.afk.kemonos.ui.R.string.saved),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(name = "Setting Video", showBackground = true)
@Composable
private fun PreviewSettingVideoScreen() {
    SettingsPreview {
        SettingVideoScreen(
            state = previewSettingState(),
            onEvent = {},
        )
    }
}
