package su.afk.kemonos.setting.presenter.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.setting.R
import kotlin.math.roundToInt

@Composable
internal fun CacheSizeSliderRow(
    title: String,
    currentMb: Int,
    minMb: Int = 200,
    maxMb: Int = 800,
    stepMb: Int = 100,
    onChangeMb: (Int) -> Unit,
) {
    val stepsCount = remember(minMb, maxMb, stepMb) {
        ((maxMb - minMb) / stepMb).coerceAtLeast(0)
    }
    val maxIndex = stepsCount

    val initialIndex = remember(currentMb, minMb, maxMb, stepMb) {
        ((currentMb.coerceIn(minMb, maxMb) - minMb).toFloat() / stepMb)
            .roundToInt()
            .coerceIn(0, maxIndex)
    }

    var index by remember(initialIndex) { mutableIntStateOf(initialIndex) }
    val currentValueMb = minMb + index * stepMb

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.settings_cache_size_value_mb, currentValueMb),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(6.dp))

        Slider(
            value = index.toFloat(),
            onValueChange = { v ->
                index = v.roundToInt().coerceIn(0, maxIndex)
            },
            valueRange = 0f..maxIndex.toFloat(),
            steps = (maxIndex - 1).coerceAtLeast(0),
            onValueChangeFinished = {
                onChangeMb(minMb + index * stepMb)
            }
        )
    }
}
