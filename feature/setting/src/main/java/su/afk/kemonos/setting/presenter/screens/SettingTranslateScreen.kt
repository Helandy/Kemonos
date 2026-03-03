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
import su.afk.kemonos.setting.presenter.view.common.SectionSpacer
import su.afk.kemonos.setting.presenter.view.common.SettingsSectionTitle
import su.afk.kemonos.setting.presenter.view.translate.TranslateLanguageRow
import su.afk.kemonos.setting.presenter.view.translate.TranslateTargetRow
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingTranslateScreen(
    state: State,
    onEvent: (Event) -> Unit,
) {
    SettingsScreenScaffold(
        title = stringResource(R.string.settings_translate_title),
        onBack = { onEvent(Event.Back) },
        isLoading = state.loading,
        contentModifier = Modifier.padding(horizontal = 8.dp),
        topBarScroll = TopBarScroll.Pinned,
    ) {

        SectionSpacer()
        SettingsSectionTitle(text = stringResource(R.string.settings_translate_title))
        Spacer(Modifier.height(6.dp))

        TranslateTargetRow(
            title = stringResource(R.string.settings_translate_title),
            value = state.uiSettingModel.translateTarget,
            onChange = {
                onEvent(Event.ChangeViewSetting.EventTranslateTarget(it))
            }
        )

        Spacer(Modifier.height(8.dp))

        TranslateLanguageRow(
            title = stringResource(R.string.settings_translate_language_title),
            languageTag = state.uiSettingModel.translateLanguageTag,
            onChange = {
                onEvent(Event.ChangeViewSetting.TranslateLanguageTag(it))
            },
        )
    }
}

@Preview(name = "Setting Translate", showBackground = true)
@Composable
private fun PreviewSettingTranslateScreen() {
    SettingsPreview {
        SettingTranslateScreen(
            state = previewSettingState(),
            onEvent = {},
        )
    }
}
