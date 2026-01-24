package su.afk.kemonos.common.utilsUI

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable

@Composable
fun KemonoAnimatedVisibility(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
        ) + expandVertically(
            animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing)
        ) + shrinkVertically(
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
        ),
    ) {
        content()
    }
}