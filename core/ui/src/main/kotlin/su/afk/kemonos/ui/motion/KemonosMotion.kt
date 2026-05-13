package su.afk.kemonos.ui.motion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset

object KemonosMotion {
    const val QuickMillis: Int = 140
    const val MediumMillis: Int = 200

    val pressScaleSpec: AnimationSpec<Float> = tween(
        durationMillis = QuickMillis,
        easing = EaseOutCubic,
    )

    val lazyItemFadeSpec: FiniteAnimationSpec<Float> = tween(
        durationMillis = MediumMillis,
        easing = EaseOutCubic,
    )

    val lazyItemPlacementSpec: FiniteAnimationSpec<IntOffset> = tween(
        durationMillis = MediumMillis,
        easing = EaseOutCubic,
    )

    val screenFadeSpec: FiniteAnimationSpec<Float> = tween(
        durationMillis = MediumMillis,
        easing = EaseOutCubic,
    )

    val itemEnter: EnterTransition = fadeIn(lazyItemFadeSpec) +
            slideInVertically(
                animationSpec = lazyItemPlacementSpec,
                initialOffsetY = { it / 10 },
            )

    val itemExit: ExitTransition = fadeOut(lazyItemFadeSpec) +
            slideOutVertically(
                animationSpec = lazyItemPlacementSpec,
                targetOffsetY = { it / 12 },
            )
}

@Composable
fun rememberKemonosPressedScale(
    interactionSource: InteractionSource,
): Float {
    val isPressed by interactionSource.collectIsPressedAsState()
    return androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = KemonosMotion.pressScaleSpec,
        label = "kemonosPressedScale",
    ).value
}

fun Modifier.kemonosPressScale(scale: Float): Modifier =
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }

@Composable
fun KemonosLazyItemMotion(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = KemonosMotion.itemEnter,
        exit = KemonosMotion.itemExit,
    ) {
        content()
    }
}
