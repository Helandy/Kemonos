package su.afk.kemonos.setting.presenter.view.uiSetting.general

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.AppThemeMode
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.preferences.ui.RandomButtonPlacement
import su.afk.kemonos.profile.R
import su.afk.kemonos.setting.presenter.view.uiSetting.AppThemeModeRow
import su.afk.kemonos.setting.presenter.view.uiSetting.RandomButtonPlacementRow
import su.afk.kemonos.setting.presenter.view.uiSetting.SwitchRow
import su.afk.kemonos.setting.presenter.view.uiSetting.common.SectionSpacer
import su.afk.kemonos.setting.presenter.view.uiSetting.common.SettingsSectionTitle
import su.afk.kemonos.setting.presenter.view.uiSetting.date.DateFormatRow
import su.afk.kemonos.setting.presenter.view.uiSetting.language.AppLanguageSettingsRow
import su.afk.kemonos.setting.presenter.view.uiSetting.language.openAppDeepLinksSettingsSafely
import su.afk.kemonos.setting.presenter.view.uiSetting.language.openAppLanguageSettingsSafely

@Composable
internal fun GeneralSettingsSection(
    suggestRandomAuthors: Boolean,
    onSuggestRandomAuthors: (Boolean) -> Unit,
    appThemeMode: AppThemeMode,
    onAppThemeMode: (AppThemeMode) -> Unit,
    dateFormatMode: DateFormatMode,
    onDateFormatMode: (DateFormatMode) -> Unit,
    randomButtonPlace: RandomButtonPlacement,
    onRandomButtonPlace: (RandomButtonPlacement) -> Unit,
) {
    val context = LocalContext.current

    SectionSpacer()
    SettingsSectionTitle(text = stringResource(R.string.settings_ui_general_title))
    Spacer(Modifier.height(6.dp))

    AppLanguageSettingsRow(
        title = stringResource(R.string.settings_ui_app_language_title),
        subtitle = stringResource(R.string.settings_ui_app_language_subtitle),
        onClick = { context.openAppLanguageSettingsSafely() }
    )

    Spacer(Modifier.height(8.dp))

    AppLanguageSettingsRow(
        title = stringResource(R.string.settings_ui_deep_links_title),
        subtitle = stringResource(R.string.settings_ui_deep_links_subtitle),
        onClick = { context.openAppDeepLinksSettingsSafely() }
    )

    Spacer(Modifier.height(8.dp))

    AppThemeModeRow(
        value = appThemeMode,
        onChange = onAppThemeMode
    )

    Spacer(Modifier.height(8.dp))

    DateFormatRow(
        title = stringResource(R.string.settings_ui_date_format_title),
        value = dateFormatMode,
        onChange = onDateFormatMode
    )

    Spacer(Modifier.height(8.dp))

    SwitchRow(
        title = stringResource(R.string.settings_ui_suggest_random_authors_title),
        checked = suggestRandomAuthors,
        onCheckedChange = onSuggestRandomAuthors,
    )

    Spacer(Modifier.height(8.dp))

    RandomButtonPlacementRow(
        value = randomButtonPlace,
        onChange = onRandomButtonPlace
    )
}
