package su.afk.kemonos.setting.presenter.view.uiSetting

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.profile.R
import kotlin.math.roundToInt

private val CacheSizeOptionsMb = listOf(100, 150, 300, 500)

/**
 * Дискретный слайдер:
 * 100 / 150 / 300 / 500 MB
 */
@Composable
internal fun CacheSizeSliderRow(
    title: String,
    currentMb: Int,
    onChangeMb: (Int) -> Unit,
) {
    val initialIndex = remember(currentMb) {
        CacheSizeOptionsMb.indexOf(currentMb).takeIf { it >= 0 } ?: 2
    }

    var index by remember(initialIndex) { mutableIntStateOf(initialIndex) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.settings_cache_size_value_mb, CacheSizeOptionsMb[index]),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(6.dp))

        Slider(
            value = index.toFloat(),
            onValueChange = { v ->
                index = v.roundToInt().coerceIn(0, CacheSizeOptionsMb.lastIndex)
            },
            valueRange = 0f..CacheSizeOptionsMb.lastIndex.toFloat(),
            steps = CacheSizeOptionsMb.size - 2, // 4 значения -> steps = 2
            onValueChangeFinished = {
                onChangeMb(CacheSizeOptionsMb[index])
            }
        )
    }
}