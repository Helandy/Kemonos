package su.afk.kemonos.profile.presenter.setting.view.uiSetting

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.CreatorViewMode
import su.afk.kemonos.profile.BuildConfig
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.presenter.setting.SettingState

@Composable
internal fun UISettingBlock(
    state: SettingState.State,
    onCreatorsViewMode: (CreatorViewMode) -> Unit,
    onSkipApiCheckOnLogin: (Boolean) -> Unit,
) {
    val ui = state.uiSettingModel ?: return

    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
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

            Spacer(Modifier.height(14.dp))

            CreatorsViewModeRow(
                value = ui.creatorsViewMode,
                onChange = onCreatorsViewMode
            )

            Spacer(Modifier.height(8.dp))

            if (BuildConfig.DEBUG) {
                DebugSwitchRow(
                    title = stringResource(R.string.settings_debug_skip_api_check_title),
                    subtitle = stringResource(R.string.settings_debug_skip_api_check_hint),
                    checked = ui.skipApiCheckOnLogin,
                    onCheckedChange = onSkipApiCheckOnLogin,
                )
            }

            // todo прочие настройки
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun UiSettingStepperRow(
    title: String,
    valueText: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    enabledMinus: Boolean,
    enabledPlus: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onMinus, enabled = enabledMinus) {
                Icon(Icons.Default.Remove, contentDescription = null)
            }

            Text(
                text = valueText,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 6.dp)
            )

            IconButton(onClick = onPlus, enabled = enabledPlus) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    }

    Divider()
}