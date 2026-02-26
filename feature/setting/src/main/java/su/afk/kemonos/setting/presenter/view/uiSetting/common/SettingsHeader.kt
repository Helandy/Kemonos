package su.afk.kemonos.setting.presenter.view.uiSetting.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.profile.R

@Composable
internal fun SettingsHeader() {
    Text(
        text = stringResource(R.string.settings_ui_title),
        style = MaterialTheme.typography.titleLarge
    )

    Spacer(Modifier.height(6.dp))

    Text(
        text = stringResource(R.string.settings_ui_hint),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
