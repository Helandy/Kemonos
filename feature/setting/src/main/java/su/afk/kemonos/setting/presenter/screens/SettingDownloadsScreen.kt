package su.afk.kemonos.setting.presenter.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.setting.R
import su.afk.kemonos.setting.presenter.SettingState.Event
import su.afk.kemonos.setting.presenter.SettingState.State
import su.afk.kemonos.setting.presenter.view.SwitchRow
import su.afk.kemonos.setting.presenter.view.common.SectionSpacer
import su.afk.kemonos.setting.presenter.view.common.SettingsSectionTitle
import su.afk.kemonos.setting.presenter.view.download.DownloadFolderModeRow
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.CenterBackTopBar
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingDownloadsScreen(
    state: State,
    onEvent: (Event) -> Unit,
) {
    BaseScreen(
        contentModifier = Modifier.padding(horizontal = 8.dp),
        isScroll = true,
        isLoading = state.loading,
        topBarScroll = TopBarScroll.Pinned,
        customTopBar = { scrollBehavior ->
            CenterBackTopBar(
                title = stringResource(R.string.settings_downloads_title),
                onBack = { onEvent(Event.Back) },
                scrollBehavior = scrollBehavior,
            )
        },
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
    }
}
