package su.afk.kemonos.setting.presenter.view.uiSetting

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import su.afk.kemonos.setting.BuildConfig
import su.afk.kemonos.setting.presenter.SettingState
import su.afk.kemonos.setting.presenter.SettingState.Event
import su.afk.kemonos.setting.presenter.SettingState.State
import su.afk.kemonos.setting.presenter.view.uiSetting.cache.CacheSettingsSection
import su.afk.kemonos.setting.presenter.view.uiSetting.common.LinksSection
import su.afk.kemonos.setting.presenter.view.uiSetting.common.SettingsHeader
import su.afk.kemonos.setting.presenter.view.uiSetting.debug.DebugSettingsSection
import su.afk.kemonos.setting.presenter.view.uiSetting.debug.DebugStorageInfoSection
import su.afk.kemonos.setting.presenter.view.uiSetting.download.DownloadSettingsSection
import su.afk.kemonos.setting.presenter.view.uiSetting.general.GeneralSettingsSection
import su.afk.kemonos.setting.presenter.view.uiSetting.translate.TranslateSettingsSection
import su.afk.kemonos.setting.presenter.view.uiSetting.viewSettings.ViewSettingsSection
import su.afk.kemonos.ui.preview.KemonosPreviewScreen

@Composable
internal fun UISettingBlock(
    state: State,
    onEvent: (Event) -> Unit,
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
            appThemeMode = ui.appThemeMode,
            onAppThemeMode = { onEvent(Event.ChangeViewSetting.EventAppThemeMode(it)) },
            dateFormatMode = ui.dateFormatMode,
            onDateFormatMode = { onEvent(Event.ChangeViewSetting.EventDateFormatMode(it)) },
            randomButtonPlace = ui.randomButtonPlacement,
            onRandomButtonPlace = { onEvent(Event.ChangeViewSetting.EventRandomButtonPlacement(it)) }
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
            onAddServiceName = { onEvent(Event.ChangeViewSetting.AddServiceName(it)) },
            onDownloadFolderMode = { onEvent(Event.ChangeViewSetting.EditDownloadFolderMode(it)) },
        )

        // todo в будущем
//        ExperimentsSection(
//            experimentalCalendar = ui.experimentalCalendar,
//            onExperimentalCalendar = { onEvent(Event.ChangeViewSetting.ExperimentalCalendar(it)) },
//            useExternalMetaData = ui.useExternalMetaData,
//            onUseExternalMetaData = { onEvent(Event.ChangeViewSetting.UseExternalMetaData(it)) },
//        )

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
