package su.afk.kemonos.creatorPost.presenter.view.swipe

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp


@Composable
internal fun SwipeArrowHint(
    modifier: Modifier,
    progress: Float, // 0..1
    isDown: Boolean,
) {
    val p = progress.coerceIn(0f, 1f)

    // визуал: плавное усиление
    val alpha = 0.25f + (0.75f * p)
    val scale = 0.85f + (0.35f * p)
    val translateY = if (isDown) (-10f * p) else (10f * p)

    Surface(
        modifier = modifier.padding(10.dp),
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.large,
    ) {
        Box(
            Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .graphicsLayer {
                    this.alpha = alpha
                    scaleX = scale
                    scaleY = scale
                    translationY = translateY
                }
        ) {
            Icon(
                imageVector = if (isDown) Icons.Rounded.KeyboardArrowDown else Icons.Rounded.KeyboardArrowUp,
                contentDescription = null,
            )
        }
    }
}