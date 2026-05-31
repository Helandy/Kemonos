package su.afk.kemonos.creatorPost.presenter.view.swipe

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


@Composable
internal fun SwipeArrowHint(
    modifier: Modifier,
    progress: Float, // 0..1
    direction: SwipeHintDirection,
) {
    val p = progress.coerceIn(0f, 1f)
    val icon = direction.icon()

    // визуал: плавное усиление
    val alpha = 0.25f + (0.75f * p)
    val scale = 0.85f + (0.35f * p)
    val translateX = when (direction) {
        SwipeHintDirection.LEFT -> 10f * p
        SwipeHintDirection.RIGHT -> -10f * p
        else -> 0f
    }
    val translateY = when (direction) {
        SwipeHintDirection.DOWN -> -10f * p
        SwipeHintDirection.UP -> 10f * p
        else -> 0f
    }

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
                    translationX = translateX
                    translationY = translateY
                }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
            )
        }
    }
}

private fun SwipeHintDirection.icon(): ImageVector =
    when (this) {
        SwipeHintDirection.DOWN -> Icons.Rounded.KeyboardArrowDown
        SwipeHintDirection.UP -> Icons.Rounded.KeyboardArrowUp
        SwipeHintDirection.LEFT -> Icons.AutoMirrored.Rounded.KeyboardArrowLeft
        SwipeHintDirection.RIGHT -> Icons.AutoMirrored.Rounded.KeyboardArrowRight
        SwipeHintDirection.NONE -> Icons.Rounded.KeyboardArrowDown
    }
