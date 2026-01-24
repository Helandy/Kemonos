package su.afk.kemonos.common.imageLoader

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun MediaWithAutoRatio(
    model: Any?,
    key: String,
    onClick: (() -> Unit)? = null,
) {
    var ratio by rememberSaveable(key) { mutableStateOf(1f) }

    AsyncImageWithStatus(
        model = model,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(ratio)
            .clip(RoundedCornerShape(6.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentScale = ContentScale.Crop,
        onSuccessSize = { size ->
            val w = size.width
            val h = size.height
            if (w.isFinite() && h.isFinite() && w > 0f && h > 0f) {
                val newRatio = w / h
                val clamped = newRatio.coerceIn(0.5f, 2.5f)
                if (abs(ratio - clamped) > 0.02f) ratio = clamped
            }
        }
    )
}
