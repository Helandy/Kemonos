package su.afk.kemonos.creatorPost.presenter.view.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs

enum class SwipeHintDirection { NONE, DOWN, UP }

@Stable
data class TikTokSwipeState(
    val modifier: Modifier,
    val dragOffsetPx: Float,
    val progress: Float,
    val direction: SwipeHintDirection,
)

@Composable
fun rememberTikTokSwipeState(
    listState: LazyListState,
    threshold: Dp = 300.dp,
    dragDamping: Float = 0.55f,

    canSwipeDownAtTop: Boolean,
    canSwipeUpAtBottom: Boolean,

    onSwipeDownAtTop: () -> Unit,
    onSwipeUpAtBottom: () -> Unit,
): TikTokSwipeState {
    // если некуда свайпать — вообще ничего не цепляем
    if (!canSwipeDownAtTop && !canSwipeUpAtBottom) {
        return TikTokSwipeState(
            modifier = Modifier,
            dragOffsetPx = 0f,
            progress = 0f,
            direction = SwipeHintDirection.NONE,
        )
    }

    val density = LocalDensity.current
    val thresholdPx = remember(threshold, density) { with(density) { threshold.toPx() } }

    // чтобы коллбеки не “застревали” старыми при рекомпозиции
    val onPrev by rememberUpdatedState(onSwipeDownAtTop)
    val onNext by rememberUpdatedState(onSwipeUpAtBottom)

    var dragDownPx by remember { mutableFloatStateOf(0f) }
    var dragUpPx by remember { mutableFloatStateOf(0f) }
    var direction by remember { mutableStateOf(SwipeHintDirection.NONE) }

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
        direction = SwipeHintDirection.NONE
    }

    fun updateVisual() {
        visualOffsetPx = when (direction) {
            SwipeHintDirection.DOWN -> (dragDownPx * dragDamping).coerceAtLeast(0f)
            SwipeHintDirection.UP -> -(dragUpPx * dragDamping).coerceAtLeast(0f)
            SwipeHintDirection.NONE -> 0f
        }
    }

    fun startSpringBack() {
        springJob?.cancel()
        springJob = scope.launch {
            // стартуем из текущего положения
            springAnim.snapTo(visualOffsetPx)
            springAnim.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow,
                    dampingRatio = Spring.DampingRatioMediumBouncy
                )
            ) {
                // this.value — текущее значение анимации на кадре
                visualOffsetPx = value
            }
            visualOffsetPx = 0f
        }
    }

    fun cancelSpringIfAny() {
        springJob?.cancel()
        springJob = null
        // если отменили во время анимации — оставим визуал как есть, дальше обновится жестом/пружинкой
    }

    val connection = remember(
        thresholdPx,
        dragDamping,
        canSwipeDownAtTop,
        canSwipeUpAtBottom,
    ) {
        object : NestedScrollConnection {

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.Drag) return Offset.Zero

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
                    direction = SwipeHintDirection.DOWN
                    dragDownPx += dy
                    updateVisual()
                    return Offset(0f, dy * 0.85f)
                }

                // ВВЕРХ на нижней границе => next
                if (dy < 0 && atBottom && canSwipeUpAtBottom) {
                    direction = SwipeHintDirection.UP
                    dragUpPx += abs(dy)
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
    val releaseModifier = Modifier.pointerInput(canSwipeDownAtTop, canSwipeUpAtBottom) {
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
        modifier = Modifier.nestedScroll(connection).then(releaseModifier),
        dragOffsetPx = visualOffsetPx,
        progress = progress,
        direction = direction,
    )
}