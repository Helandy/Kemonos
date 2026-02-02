package su.afk.kemonos.profile.presenter.setting.view.uiSetting.download

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.DownloadFolderMode
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.SwitchRow
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.common.SectionSpacer
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.common.SettingsSectionTitle

@Composable
internal fun DownloadSettingsSection(
    addServiceName: Boolean,
    downloadFolderMode: DownloadFolderMode,
    onAddServiceName: (Boolean) -> Unit,
    onDownloadFolderMode: (DownloadFolderMode) -> Unit,
) {
    SectionSpacer()
    SettingsSectionTitle(text = stringResource(R.string.settings_downloads_title))
    Spacer(Modifier.height(6.dp))

    SwitchRow(
        title = stringResource(R.string.settings_download_add_service_title),
        checked = addServiceName,
        onCheckedChange = onAddServiceName
    )

    Spacer(Modifier.height(6.dp))

    DownloadFolderModeRow(
        title = stringResource(R.string.settings_download_folder_mode_title),
        value = downloadFolderMode,
        addServiceName = addServiceName,
        onChange = onDownloadFolderMode
    )
}
