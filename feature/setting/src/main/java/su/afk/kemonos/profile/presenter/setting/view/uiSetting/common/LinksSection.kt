package su.afk.kemonos.profile.presenter.setting.view.uiSetting.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import su.afk.kemonos.profile.R

@Composable
internal fun LinksSection() {
    SectionSpacer()
    SettingsSectionTitle(text = stringResource(R.string.settings_links_title))
}