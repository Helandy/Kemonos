package su.afk.kemonos.profile.presenter.setting.view.uiSetting.general

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.preferences.ui.RandomButtonPlacement
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.RandomButtonPlacementRow
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.SwitchRow
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.common.SectionSpacer
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.common.SettingsSectionTitle
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.date.DateFormatRow

@Composable
internal fun GeneralSettingsSection(
    suggestRandomAuthors: Boolean,
    onSuggestRandomAuthors: (Boolean) -> Unit,
    dateFormatMode: DateFormatMode,
    onDateFormatMode: (DateFormatMode) -> Unit,
    randomButtonPlace: RandomButtonPlacement,
    onRandomButtonPlace: (RandomButtonPlacement) -> Unit,
) {
    SectionSpacer()
    SettingsSectionTitle(text = stringResource(R.string.settings_ui_general_title))
    Spacer(Modifier.height(6.dp))

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
