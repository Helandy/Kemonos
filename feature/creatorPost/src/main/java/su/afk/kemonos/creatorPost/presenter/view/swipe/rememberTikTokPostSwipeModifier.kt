package su.afk.kemonos.creatorPost.presenter.view.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import su.afk.kemonos.preferences.ui.PostSwipeAxis
import su.afk.kemonos.preferences.ui.PostSwipeFeel
import kotlin.math.abs

enum class SwipeHintDirection { NONE, DOWN, UP, LEFT, RIGHT }

@Stable
data class TikTokSwipeState(
    val modifier: Modifier,
    val dragOffsetPx: Offset,
    val progress: Float,
    val direction: SwipeHintDirection,
)

@Composable
fun rememberTikTokSwipeState(
    listState: LazyListState,
    postSwipeAxis: PostSwipeAxis,
    postSwipeFeel: PostSwipeFeel,

    canSwipeDownAtTop: Boolean,
    canSwipeUpAtBottom: Boolean,
    hapticFeedbackEnabled: Boolean = true,

    onSwipeDownAtTop: () -> Unit,
    onSwipeUpAtBottom: () -> Unit,
): TikTokSwipeState {
    // если некуда свайпать — вообще ничего не цепляем
    if (!canSwipeDownAtTop && !canSwipeUpAtBottom) {
        return TikTokSwipeState(
            modifier = Modifier,
            dragOffsetPx = Offset.Zero,
            progress = 0f,
            direction = SwipeHintDirection.NONE,
        )
    }

    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val feelConfig = remember(postSwipeFeel, postSwipeAxis) {
        postSwipeFeel.toSwipeFeelConfig(postSwipeAxis)
    }
    val threshold = feelConfig.threshold
    val dragDamping = feelConfig.dragDamping
    val thresholdPx = remember(threshold, density) { with(density) { threshold.toPx() } }

    // чтобы коллбеки не “застревали” старыми при рекомпозиции
    val onPrev by rememberUpdatedState(onSwipeDownAtTop)
    val onNext by rememberUpdatedState(onSwipeUpAtBottom)

    var dragDownPx by remember { mutableFloatStateOf(0f) }
    var dragUpPx by remember { mutableFloatStateOf(0f) }
    var dragLeftPx by remember { mutableFloatStateOf(0f) }
    var dragRightPx by remember { mutableFloatStateOf(0f) }
    var direction by remember { mutableStateOf(SwipeHintDirection.NONE) }
    var thresholdHapticDirection by remember { mutableStateOf(SwipeHintDirection.NONE) }

    var visualOffsetPx by remember { mutableFloatStateOf(0f) }

    val springAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var springJob by remember { mutableStateOf<Job?>(null) }

    val atTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0 }
    }
    val atBottom by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val lastVisible = info.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
            lastVisible.index == info.totalItemsCount - 1
        }
    }

    fun resetCounters() {
        dragDownPx = 0f
        dragUpPx = 0f
        dragLeftPx = 0f
        dragRightPx = 0f
        direction = SwipeHintDirection.NONE
        thresholdHapticDirection = SwipeHintDirection.NONE
    }

    fun updateVisual() {
        visualOffsetPx = when (direction) {
            SwipeHintDirection.DOWN -> (dragDownPx * dragDamping).coerceAtLeast(0f)
            SwipeHintDirection.UP -> -(dragUpPx * dragDamping).coerceAtLeast(0f)
            SwipeHintDirection.LEFT -> -(dragLeftPx * dragDamping).coerceAtLeast(0f)
            SwipeHintDirection.RIGHT -> (dragRightPx * dragDamping).coerceAtLeast(0f)
            SwipeHintDirection.NONE -> 0f
        }
    }

    fun startSpringBack() {
        springJob?.cancel()
        springJob = scope.launch {
            when (val springBack = feelConfig.springBack) {
                SwipeSpringBack.Snap -> {
                    springAnim.snapTo(0f)
                    visualOffsetPx = 0f
                }

                is SwipeSpringBack.Spring -> {
                    // стартуем из текущего положения
                    springAnim.snapTo(visualOffsetPx)
                    springAnim.animateTo(
                        targetValue = 0f,
                        animationSpec = spring(
                            stiffness = springBack.stiffness,
                            dampingRatio = springBack.dampingRatio
                        )
                    ) {
                        // this.value — текущее значение анимации на кадре
                        visualOffsetPx = value
                    }
                    visualOffsetPx = 0f
                }
            }
        }
    }

    fun cancelSpringIfAny() {
        springJob?.cancel()
        springJob = null
        // если отменили во время анимации — оставим визуал как есть, дальше обновится жестом/пружинкой
    }

    fun startDirection(newDirection: SwipeHintDirection) {
        if (hapticFeedbackEnabled && direction != newDirection) {
            haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
        }
        direction = newDirection
    }

    fun maybePerformThresholdHaptic() {
        val crossedThreshold = when (direction) {
            SwipeHintDirection.DOWN -> dragDownPx >= thresholdPx
            SwipeHintDirection.UP -> dragUpPx >= thresholdPx
            SwipeHintDirection.LEFT -> dragLeftPx >= thresholdPx
            SwipeHintDirection.RIGHT -> dragRightPx >= thresholdPx
            SwipeHintDirection.NONE -> false
        }

        if (hapticFeedbackEnabled && crossedThreshold && thresholdHapticDirection != direction) {
            haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
            thresholdHapticDirection = direction
        }
    }

    if (postSwipeAxis == PostSwipeAxis.HORIZONTAL) {
        val horizontalModifier = Modifier.pointerInput(
            thresholdPx,
            dragDamping,
            canSwipeDownAtTop,
            canSwipeUpAtBottom,
            hapticFeedbackEnabled,
            postSwipeFeel,
            postSwipeAxis,
        ) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                cancelSpringIfAny()

                var totalX = 0f
                var totalY = 0f
                var lockedHorizontal = false
                var pressed = true

                do {
                    val event = awaitPointerEvent()
                    pressed = event.changes.any { it.pressed }
                    val change = event.changes.firstOrNull() ?: continue
                    val dx = change.positionChange().x
                    val dy = change.positionChange().y

                    totalX += dx
                    totalY += dy

                    if (!lockedHorizontal) {
                        val horizontalIntent = abs(totalX) > viewConfiguration.touchSlop &&
                                abs(totalX) > abs(totalY)
                        if (!horizontalIntent) continue

                        val canStartLeft = totalX < 0 && canSwipeUpAtBottom
                        val canStartRight = totalX > 0 && canSwipeDownAtTop
                        if (!canStartLeft && !canStartRight) continue

                        lockedHorizontal = true
                        startDirection(
                            if (totalX < 0) SwipeHintDirection.LEFT else SwipeHintDirection.RIGHT
                        )
                    }

                    if (lockedHorizontal) {
                        when {
                            direction == SwipeHintDirection.LEFT && dx > 0 -> {
                                dragLeftPx = (dragLeftPx - dx).coerceAtLeast(0f)
                                if (dragLeftPx == 0f) direction = SwipeHintDirection.NONE
                            }

                            direction == SwipeHintDirection.RIGHT && dx < 0 -> {
                                dragRightPx = (dragRightPx - abs(dx)).coerceAtLeast(0f)
                                if (dragRightPx == 0f) direction = SwipeHintDirection.NONE
                            }

                            dx < 0 && canSwipeUpAtBottom -> {
                                startDirection(SwipeHintDirection.LEFT)
                                dragLeftPx += abs(dx)
                            }

                            dx > 0 && canSwipeDownAtTop -> {
                                startDirection(SwipeHintDirection.RIGHT)
                                dragRightPx += dx
                            }
                        }

                        maybePerformThresholdHaptic()
                        updateVisual()
                        change.consume()
                    }
                } while (pressed)

                val triggered = when (direction) {
                    SwipeHintDirection.LEFT -> dragLeftPx >= thresholdPx && canSwipeUpAtBottom
                    SwipeHintDirection.RIGHT -> dragRightPx >= thresholdPx && canSwipeDownAtTop
                    else -> false
                }

                if (triggered && hapticFeedbackEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                }

                if (triggered) {
                    if (direction == SwipeHintDirection.LEFT) onNext()
                    if (direction == SwipeHintDirection.RIGHT) onPrev()
                }

                resetCounters()
                startSpringBack()
            }
        }

        val progress = when (direction) {
            SwipeHintDirection.LEFT -> (dragLeftPx / thresholdPx).coerceIn(0f, 1f)
            SwipeHintDirection.RIGHT -> (dragRightPx / thresholdPx).coerceIn(0f, 1f)
            else -> 0f
        }

        return TikTokSwipeState(
            modifier = horizontalModifier,
            dragOffsetPx = Offset(visualOffsetPx, 0f),
            progress = progress,
            direction = direction,
        )
    }

    val connection = remember(
        thresholdPx,
        dragDamping,
        canSwipeDownAtTop,
        canSwipeUpAtBottom,
        hapticFeedbackEnabled,
        haptic,
    ) {
        object : NestedScrollConnection {

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.UserInput) return Offset.Zero

                val dy = available.y

                // как только пользователь снова тянет — отменяем “пружинку”
                cancelSpringIfAny()

                // ОТКАТ пальцем назад должен уменьшать накопление (чтобы не “залипало”)
                if (direction == SwipeHintDirection.DOWN && dy < 0) {
                    dragDownPx = (dragDownPx - abs(dy)).coerceAtLeast(0f)
                    if (dragDownPx == 0f) direction = SwipeHintDirection.NONE
                    updateVisual()
                    return Offset(0f, dy * 0.85f)
                }

                if (direction == SwipeHintDirection.UP && dy > 0) {
                    dragUpPx = (dragUpPx - dy).coerceAtLeast(0f)
                    if (dragUpPx == 0f) direction = SwipeHintDirection.NONE
                    updateVisual()
                    return Offset(0f, dy * 0.85f)
                }

                // ВНИЗ на верхней границе => prev
                if (dy > 0 && atTop && canSwipeDownAtTop) {
                    startDirection(SwipeHintDirection.DOWN)
                    dragDownPx += dy
                    maybePerformThresholdHaptic()
                    updateVisual()
                    return Offset(0f, dy * 0.85f)
                }

                // ВВЕРХ на нижней границе => next
                if (dy < 0 && atBottom && canSwipeUpAtBottom) {
                    startDirection(SwipeHintDirection.UP)
                    dragUpPx += abs(dy)
                    maybePerformThresholdHaptic()
                    updateVisual()
                    return Offset(0f, dy * 0.85f)
                }

                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                val triggered = when (direction) {
                    SwipeHintDirection.DOWN -> dragDownPx >= thresholdPx && canSwipeDownAtTop
                    SwipeHintDirection.UP -> dragUpPx >= thresholdPx && canSwipeUpAtBottom
                    else -> false
                }

                if (triggered && hapticFeedbackEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                }

                if (triggered) {
                    if (direction == SwipeHintDirection.DOWN) onPrev()
                    if (direction == SwipeHintDirection.UP) onNext()
                }

                resetCounters()
                startSpringBack()
                return Velocity.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                // если fling нулевой/короткий — всё равно вернуть назад
                resetCounters()
                startSpringBack()
                return Velocity.Zero
            }
        }
    }

    // отпуск пальца БЕЗ fling (иначе может оставаться “пол экрана занятым”)
    val releaseModifier = Modifier.pointerInput(canSwipeDownAtTop, canSwipeUpAtBottom, postSwipeFeel, postSwipeAxis) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false)
            waitForUpOrCancellation()
            // если отпустили/отменили, а мы в режиме свайпа — вернёмся назад
            if (direction != SwipeHintDirection.NONE) {
                resetCounters()
                startSpringBack()
            }
        }
    }

    val progress = when (direction) {
        SwipeHintDirection.DOWN -> (dragDownPx / thresholdPx).coerceIn(0f, 1f)
        SwipeHintDirection.UP -> (dragUpPx / thresholdPx).coerceIn(0f, 1f)
        else -> 0f
    }

    return TikTokSwipeState(
        modifier = Modifier
            .nestedScroll(connection)
            .then(releaseModifier),
        dragOffsetPx = Offset(0f, visualOffsetPx),
        progress = progress,
        direction = direction,
    )
}

private data class SwipeFeelConfig(
    val threshold: Dp,
    val dragDamping: Float,
    val springBack: SwipeSpringBack,
)

private sealed interface SwipeSpringBack {
    data object Snap : SwipeSpringBack

    data class Spring(
        val stiffness: Float,
        val dampingRatio: Float,
    ) : SwipeSpringBack
}

private fun PostSwipeFeel.toSwipeFeelConfig(axis: PostSwipeAxis): SwipeFeelConfig =
    when (axis) {
        PostSwipeAxis.VERTICAL -> toVerticalSwipeFeelConfig()
        PostSwipeAxis.HORIZONTAL -> toHorizontalSwipeFeelConfig()
    }

private fun PostSwipeFeel.toVerticalSwipeFeelConfig(): SwipeFeelConfig =
    when (this) {
        PostSwipeFeel.EFFORTLESS -> SwipeFeelConfig(
            threshold = 96.dp,
            dragDamping = 1.0f,
            springBack = SwipeSpringBack.Snap,
        )

        PostSwipeFeel.LIGHT -> SwipeFeelConfig(
            threshold = 180.dp,
            dragDamping = 0.75f,
            springBack = SwipeSpringBack.Spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioNoBouncy,
            ),
        )

        PostSwipeFeel.NORMAL -> SwipeFeelConfig(
            threshold = 300.dp,
            dragDamping = 0.55f,
            springBack = SwipeSpringBack.Spring(
                stiffness = Spring.StiffnessMediumLow,
                dampingRatio = Spring.DampingRatioMediumBouncy,
            ),
        )

        PostSwipeFeel.FIRM -> SwipeFeelConfig(
            threshold = 420.dp,
            dragDamping = 0.4f,
            springBack = SwipeSpringBack.Spring(
                stiffness = Spring.StiffnessMedium,
                dampingRatio = Spring.DampingRatioNoBouncy,
            ),
        )
    }

private fun PostSwipeFeel.toHorizontalSwipeFeelConfig(): SwipeFeelConfig =
    when (this) {
        PostSwipeFeel.EFFORTLESS -> SwipeFeelConfig(
            threshold = 72.dp,
            dragDamping = 1.0f,
            springBack = SwipeSpringBack.Snap,
        )

        PostSwipeFeel.LIGHT -> SwipeFeelConfig(
            threshold = 128.dp,
            dragDamping = 0.85f,
            springBack = SwipeSpringBack.Spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioNoBouncy,
            ),
        )

        PostSwipeFeel.NORMAL -> SwipeFeelConfig(
            threshold = 180.dp,
            dragDamping = 0.75f,
            springBack = SwipeSpringBack.Spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioNoBouncy,
            ),
        )

        PostSwipeFeel.FIRM -> SwipeFeelConfig(
            threshold = 300.dp,
            dragDamping = 0.55f,
            springBack = SwipeSpringBack.Spring(
                stiffness = Spring.StiffnessMediumLow,
                dampingRatio = Spring.DampingRatioMediumBouncy,
            ),
        )
    }
