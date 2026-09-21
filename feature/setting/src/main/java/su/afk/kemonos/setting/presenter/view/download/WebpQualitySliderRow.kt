package su.afk.kemonos.setting.presenter.view.download

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.setting.R
import kotlin.math.roundToInt

@Composable
internal fun WebpQualitySliderRow(
    quality: Int,
    enabled: Boolean,
    onChange: (Int) -> Unit,
) {
    val min = UiSettingModel.MIN_DOWNLOAD_WEBP_QUALITY
    val max = UiSettingModel.MAX_DOWNLOAD_WEBP_QUALITY
    val step = UiSettingModel.DOWNLOAD_WEBP_QUALITY_STEP

    val maxIndex = remember(min, max, step) { ((max - min) / step).coerceAtLeast(0) }
    val initialIndex = remember(quality, min, max, step) {
        ((quality.coerceIn(min, max) - min).toFloat() / step).roundToInt().coerceIn(0, maxIndex)
    }

    var index by remember(initialIndex) { mutableIntStateOf(initialIndex) }
    val currentQuality = min + index * step

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.settings_download_webp_quality_title),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = currentQuality.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Text(
            text = stringResource(R.string.settings_download_webp_quality_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(6.dp))

        Slider(
            value = index.toFloat(),
            enabled = enabled,
            onValueChange = { value -> index = value.roundToInt().coerceIn(0, maxIndex) },
            valueRange = 0f..maxIndex.toFloat(),
            steps = (maxIndex - 1).coerceAtLeast(0),
            onValueChangeFinished = { onChange(min + index * step) },
        )
    }
}
