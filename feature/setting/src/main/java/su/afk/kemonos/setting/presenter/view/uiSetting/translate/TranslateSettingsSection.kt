package su.afk.kemonos.setting.presenter.view.uiSetting.translate

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.TranslateTarget
import su.afk.kemonos.profile.R
import su.afk.kemonos.setting.presenter.view.uiSetting.common.SectionSpacer
import su.afk.kemonos.setting.presenter.view.uiSetting.common.SettingsSectionTitle

@Composable
internal fun TranslateSettingsSection(
    translateTarget: TranslateTarget,
    translateLanguageTag: String,
    onTranslateTarget: (TranslateTarget) -> Unit,
    onTranslateLanguageTag: (String) -> Unit,
) {
    SectionSpacer()
    SettingsSectionTitle(text = stringResource(R.string.settings_translate_title))
    Spacer(Modifier.height(6.dp))

    TranslateTargetRow(
        title = stringResource(R.string.settings_translate_title),
        value = translateTarget,
        onChange = onTranslateTarget
    )

    Spacer(Modifier.height(8.dp))

    TranslateLanguageRow(
        title = stringResource(R.string.settings_translate_language_title),
        languageTag = translateLanguageTag,
        onChange = onTranslateLanguageTag,
    )
}
