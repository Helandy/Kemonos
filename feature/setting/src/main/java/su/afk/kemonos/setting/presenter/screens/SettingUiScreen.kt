package su.afk.kemonos.setting.presenter.screens

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.setting.BuildConfig
import su.afk.kemonos.setting.R
import su.afk.kemonos.setting.presenter.SettingState.Event
import su.afk.kemonos.setting.presenter.SettingState.State
import su.afk.kemonos.setting.presenter.view.common.SectionSpacer
import su.afk.kemonos.setting.presenter.view.debug.DebugSettingsSection
import su.afk.kemonos.setting.presenter.view.general.GeneralSettingsSection
import su.afk.kemonos.setting.presenter.view.language.AppLanguageSettingsRow
import su.afk.kemonos.setting.presenter.view.viewSettings.ViewSettingsSection
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingUiScreen(
    state: State,
    onEvent: (Event) -> Unit,
) {
    val ui = state.uiSettingModel

    SettingsScreenScaffold(
        title = stringResource(R.string.settings_ui_title),
        onBack = { onEvent(Event.Back) },
        isLoading = state.loading,
        contentModifier = Modifier.padding(horizontal = 8.dp),
        topBarScroll = TopBarScroll.ExitUntilCollapsed,
    ) {
        val interactionSource = remember { MutableInteractionSource() }

        Text(
            text = stringResource(R.string.settings_ui_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {},
                    onLongClick = { onEvent(Event.OpenDebugStorageSettings) },
                )
        )

        if (BuildConfig.DEBUG) {
            DebugSettingsSection(
                enabled = true,
                skipApiCheckOnLogin = ui.skipApiCheckOnLogin,
                onSkipApiCheckOnLogin = {
                    onEvent(Event.ChangeViewSetting.SkipApiCheckOnLogin(it))
                },
            )
        }

        GeneralSettingsSection(
            suggestRandomAuthors = ui.suggestRandomAuthors,
            onSuggestRandomAuthors = { onEvent(Event.ChangeViewSetting.SuggestRandomAuthors(it)) },
            showKemono = ui.showKemono,
            showCoomer = ui.showCoomer,
            defaultSite = ui.defaultSite,
            onSiteDisplayModeChanged = { onEvent(Event.ChangeViewSetting.SiteDisplayModeChanged(it)) },
            appThemeMode = ui.appThemeMode,
            onAppThemeMode = { onEvent(Event.ChangeViewSetting.EventAppThemeMode(it)) },
            dateFormatMode = ui.dateFormatMode,
            onDateFormatMode = { onEvent(Event.ChangeViewSetting.EventDateFormatMode(it)) },
            randomButtonPlace = ui.randomButtonPlacement,
            onRandomButtonPlace = { onEvent(Event.ChangeViewSetting.EventRandomButtonPlacement(it)) },
        )

        SectionSpacer()

        AppLanguageSettingsRow(
            title = stringResource(R.string.settings_ui_creator_profile_tabs_sort_title),
            subtitle = stringResource(R.string.settings_ui_creator_profile_tabs_sort_hint),
            onClick = { onEvent(Event.OpenCreatorTabsOrderEditor) },
        )

        ViewSettingsSection(
            ui = ui,
            onEvent = onEvent,
        )

        SectionSpacer()
    }
}

@Preview(name = "Setting UI", showBackground = true)
@Composable
private fun PreviewSettingUiScreen() {
    SettingsPreview {
        SettingUiScreen(
            state = previewSettingState(),
            onEvent = {},
        )
    }
}
