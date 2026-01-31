package su.afk.kemonos.profile.presenter.setting.view.uiSetting

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import su.afk.kemonos.common.utilsUI.KemonosPreviewScreen
import su.afk.kemonos.profile.BuildConfig
import su.afk.kemonos.profile.presenter.setting.SettingState.Event
import su.afk.kemonos.profile.presenter.setting.SettingState.State
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.cache.CacheSettingsSection
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.common.LinksSection
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.common.SettingsHeader
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.debug.DebugSettingsSection
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.debug.DebugStorageInfoSection
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.download.DownloadSettingsSection
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.experiment.ExperimentsSection
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.general.GeneralSettingsSection
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.translate.TranslateSettingsSection
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.viewSettings.ViewSettingsSection

@Composable
internal fun UISettingBlock(
    state: State,
    onEvent: (Event) -> Unit
) {
    val ui = state.uiSettingModel

    Column {
        SettingsHeader()

        if (BuildConfig.DEBUG) {
            DebugSettingsSection(
                enabled = true,
                skipApiCheckOnLogin = ui.skipApiCheckOnLogin,
                onSkipApiCheckOnLogin = { onEvent(Event.ChangeViewSetting.SkipApiCheckOnLogin(it)) },
            )

            DebugStorageInfoSection(enabled = true)
        }

        GeneralSettingsSection(
            suggestRandomAuthors = ui.suggestRandomAuthors,
            onSuggestRandomAuthors = { onEvent(Event.ChangeViewSetting.SuggestRandomAuthors(it)) },
            dateFormatMode = ui.dateFormatMode,
            onDateFormatMode = { onEvent(Event.ChangeViewSetting.EventDateFormatMode(it)) },
        )

        TranslateSettingsSection(
            translateTarget = ui.translateTarget,
            translateLanguageTag = ui.translateLanguageTag,
            onTranslateTarget = { onEvent(Event.ChangeViewSetting.EventTranslateTarget(it)) },
            onTranslateLanguageTag = { onEvent(Event.ChangeViewSetting.TranslateLanguageTag(it)) },
        )

        ViewSettingsSection(
            ui = ui,
            onEvent = onEvent,
        )

        CacheSettingsSection(
            coilCacheSizeMb = ui.coilCacheSizeMb,
            previewVideoSizeMb = ui.previewVideoSizeMb,
            onCoilCache = { onEvent(Event.ChangeViewSetting.CoilCacheSizeMb(it)) },
            onPreviewCache = { onEvent(Event.ChangeViewSetting.PreviewVideoSizeMb(it)) },
        )

        DownloadSettingsSection(
            addServiceName = ui.addServiceName,
            downloadFolderMode = ui.downloadFolderMode,
            useExternalMetaData = ui.useExternalMetaData,
            onAddServiceName = { onEvent(Event.ChangeViewSetting.AddServiceName(it)) },
            onDownloadFolderMode = { onEvent(Event.ChangeViewSetting.EditDownloadFolderMode(it)) },
            onUseExternalMetaData = { onEvent(Event.ChangeViewSetting.UseExternalMetaData(it)) },
        )

        ExperimentsSection(
            experimentalCalendar = ui.experimentalCalendar,
            onExperimentalCalendar = { onEvent(Event.ChangeViewSetting.ExperimentalCalendar(it)) },
        )

        LinksSection()
    }
}

@Preview("PreviewUISettingBlock")
@Composable
private fun PreviewUISettingBlock() {
    KemonosPreviewScreen {
        UISettingBlock(
            state = State(),
            onEvent = {},
        )
    }
}