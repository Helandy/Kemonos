package su.afk.kemonos.setting.presenter.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.setting.R
import su.afk.kemonos.setting.presenter.SettingState.Event
import su.afk.kemonos.setting.presenter.SettingState.State
import su.afk.kemonos.setting.presenter.view.SwitchRow
import su.afk.kemonos.setting.presenter.view.common.SectionSpacer
import su.afk.kemonos.setting.presenter.view.common.SettingsSectionTitle
import su.afk.kemonos.setting.presenter.view.download.DownloadFileNameModeRow
import su.afk.kemonos.setting.presenter.view.download.DownloadFolderModeRow
import su.afk.kemonos.setting.presenter.view.download.WebpQualitySliderRow
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingDownloadsScreen(
    state: State,
    onEvent: (Event) -> Unit,
) {
    SettingsScreenScaffold(
        title = stringResource(R.string.settings_downloads_title),
        onBack = { onEvent(Event.Back) },
        isLoading = state.loading,
        contentModifier = Modifier.padding(horizontal = 8.dp),
        topBarScroll = TopBarScroll.Pinned,
    ) {
        SectionSpacer()
        SettingsSectionTitle(text = stringResource(R.string.settings_downloads_title))
        Spacer(Modifier.height(6.dp))

        SwitchRow(
            title = stringResource(R.string.settings_download_add_service_title),
            checked = state.uiSettingModel.addServiceName,
            onCheckedChange = { onEvent(Event.ChangeViewSetting.AddServiceName(it)) }
        )

        Spacer(Modifier.height(6.dp))

        DownloadFolderModeRow(
            title = stringResource(R.string.settings_download_folder_mode_title),
            value = state.uiSettingModel.downloadFolderMode,
            addServiceName = state.uiSettingModel.addServiceName,
            onChange = { onEvent(Event.ChangeViewSetting.EditDownloadFolderMode(it)) }
        )

        SectionSpacer()

        DownloadFileNameModeRow(
            title = stringResource(R.string.settings_download_file_name_mode_title),
            value = state.uiSettingModel.downloadFileNameMode,
            onChange = { onEvent(Event.ChangeViewSetting.EditDownloadFileNameMode(it)) }
        )

        Spacer(Modifier.height(6.dp))

        SwitchRow(
            title = stringResource(R.string.settings_download_post_cover_title),
            subtitle = stringResource(R.string.settings_download_post_cover_subtitle),
            checked = state.uiSettingModel.downloadPostCover,
            onCheckedChange = { onEvent(Event.ChangeViewSetting.DownloadPostCover(it)) }
        )

        Spacer(Modifier.height(6.dp))

        SwitchRow(
            title = stringResource(R.string.settings_download_single_notification_title),
            subtitle = stringResource(R.string.settings_download_single_notification_subtitle),
            checked = state.uiSettingModel.downloadSingleNotification,
            onCheckedChange = { onEvent(Event.ChangeViewSetting.DownloadSingleNotification(it)) }
        )

        Spacer(Modifier.height(6.dp))

        SwitchRow(
            title = stringResource(R.string.settings_download_webp_title),
            subtitle = stringResource(R.string.settings_download_webp_subtitle),
            checked = state.uiSettingModel.downloadConvertToWebp,
            onCheckedChange = { onEvent(Event.ChangeViewSetting.DownloadConvertToWebp(it)) }
        )

        if (state.uiSettingModel.downloadConvertToWebp) {
            Spacer(Modifier.height(6.dp))

            WebpQualitySliderRow(
                quality = state.uiSettingModel.downloadWebpQuality,
                enabled = true,
                onChange = { onEvent(Event.ChangeViewSetting.DownloadWebpQuality(it)) },
            )
        }
    }
}

@Preview(name = "Setting Downloads", showBackground = true)
@Composable
private fun PreviewSettingDownloadsScreen() {
    SettingsPreview {
        SettingDownloadsScreen(
            state = previewSettingState(),
            onEvent = {},
        )
    }
}
