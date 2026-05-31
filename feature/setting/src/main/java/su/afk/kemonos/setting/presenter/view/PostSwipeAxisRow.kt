package su.afk.kemonos.setting.presenter.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.PostSwipeAxis
import su.afk.kemonos.preferences.ui.PostSwipeFeel
import su.afk.kemonos.setting.R
import su.afk.kemonos.setting.presenter.view.common.settingsSegmentedButtonColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PostSwipeAxisRow(
    value: PostSwipeAxis,
    feel: PostSwipeFeel,
    onChange: (PostSwipeAxis) -> Unit,
    onFeelChange: (PostSwipeFeel) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_post_swipe_title),
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(Modifier.height(8.dp))

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = value == PostSwipeAxis.VERTICAL,
                onClick = { onChange(PostSwipeAxis.VERTICAL) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                colors = settingsSegmentedButtonColors(),
                label = { Text(stringResource(R.string.settings_post_swipe_vertical)) },
            )
            SegmentedButton(
                selected = value == PostSwipeAxis.HORIZONTAL,
                onClick = { onChange(PostSwipeAxis.HORIZONTAL) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                colors = settingsSegmentedButtonColors(),
                label = { Text(stringResource(R.string.settings_post_swipe_horizontal)) },
            )
        }

        Spacer(Modifier.height(8.dp))

        PostSwipeFeelSetting(
            value = feel,
            onChange = onFeelChange,
        )
    }
    HorizontalDivider()
}

@Composable
private fun PostSwipeFeelSetting(
    value: PostSwipeFeel,
    onChange: (PostSwipeFeel) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_post_swipe_feel_title),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )

        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(text = value.label())
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                PostSwipeFeel.entries.forEach { mode ->
                    DropdownMenuItem(
                        text = { Text(mode.label()) },
                        onClick = {
                            expanded = false
                            onChange(mode)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PostSwipeFeel.label(): String =
    when (this) {
        PostSwipeFeel.EFFORTLESS -> stringResource(R.string.settings_post_swipe_feel_effortless)
        PostSwipeFeel.LIGHT -> stringResource(R.string.settings_post_swipe_feel_light)
        PostSwipeFeel.NORMAL -> stringResource(R.string.settings_post_swipe_feel_normal)
        PostSwipeFeel.FIRM -> stringResource(R.string.settings_post_swipe_feel_firm)
    }
