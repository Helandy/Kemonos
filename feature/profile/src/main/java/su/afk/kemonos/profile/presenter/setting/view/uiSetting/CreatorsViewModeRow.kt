package su.afk.kemonos.profile.presenter.setting.view.uiSetting

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.CreatorViewMode
import su.afk.kemonos.profile.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatorsViewModeRow(
    value: CreatorViewMode,
    onChange: (CreatorViewMode) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.settings_ui_creators_view_mode),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        SingleChoiceSegmentedButtonRow {
            SegmentedButton(
                selected = value == CreatorViewMode.LIST,
                onClick = { onChange(CreatorViewMode.LIST) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                label = { Text(stringResource(R.string.settings_ui_view_mode_list)) },
            )
            SegmentedButton(
                selected = value == CreatorViewMode.GRID,
                onClick = { onChange(CreatorViewMode.GRID) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                label = { Text(stringResource(R.string.settings_ui_view_mode_grid)) },
            )
        }
    }

    Divider()
}
