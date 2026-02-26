package su.afk.kemonos.setting.presenter.view.uiSetting

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.AppThemeMode
import su.afk.kemonos.profile.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppThemeModeRow(
    value: AppThemeMode,
    onChange: (AppThemeMode) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.settings_theme_title),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        SingleChoiceSegmentedButtonRow {
            SegmentedButton(
                selected = value == AppThemeMode.SYSTEM,
                onClick = { onChange(AppThemeMode.SYSTEM) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                label = { Text(stringResource(R.string.settings_theme_system)) },
            )
            SegmentedButton(
                selected = value == AppThemeMode.LIGHT,
                onClick = { onChange(AppThemeMode.LIGHT) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                label = { Text(stringResource(R.string.settings_theme_light)) },
            )
            SegmentedButton(
                selected = value == AppThemeMode.DARK,
                onClick = { onChange(AppThemeMode.DARK) },
                shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                label = { Text(stringResource(R.string.settings_theme_dark)) },
            )
        }
    }
    Divider()
}
