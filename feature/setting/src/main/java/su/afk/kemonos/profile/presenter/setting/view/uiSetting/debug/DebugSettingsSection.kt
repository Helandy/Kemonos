package su.afk.kemonos.profile.presenter.setting.view.uiSetting.debug

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.common.SectionSpacer
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.common.SettingsSectionTitle

@Composable
internal fun DebugSettingsSection(
    enabled: Boolean,
    skipApiCheckOnLogin: Boolean,
    onSkipApiCheckOnLogin: (Boolean) -> Unit,
) {
    if (!enabled) return

    SectionSpacer()
    SettingsSectionTitle(text = stringResource(R.string.settings_debug_title))
    Spacer(Modifier.height(6.dp))

    DebugSwitchRow(
        title = stringResource(R.string.settings_debug_skip_api_check_title),
        subtitle = stringResource(R.string.settings_debug_skip_api_check_hint),
        checked = skipApiCheckOnLogin,
        onCheckedChange = onSkipApiCheckOnLogin,
    )
}
