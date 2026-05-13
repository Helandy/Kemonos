package su.afk.kemonos.ui.haptic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
fun rememberPullRefreshWithHaptic(
    onRefresh: () -> Unit,
): () -> Unit {
    val haptic = LocalHapticFeedback.current
    return remember(haptic, onRefresh) {
        {
            haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
            onRefresh()
        }
    }
}

@Composable
fun PostListScrollHapticEffect(
    listState: LazyListState,
) {
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(listState, haptic) {
        var previous = ScrollHapticSnapshot(
            index = listState.firstVisibleItemIndex,
            offset = listState.firstVisibleItemScrollOffset,
            isScrolling = listState.isScrollInProgress,
        )
        var lastDirection = ScrollDirection.None

        snapshotFlow {
            ScrollHapticSnapshot(
                index = listState.firstVisibleItemIndex,
                offset = listState.firstVisibleItemScrollOffset,
                isScrolling = listState.isScrollInProgress,
            )
        }.collect { current ->
            lastDirection = handleScrollHaptic(
                previous = previous,
                current = current,
                lastDirection = lastDirection,
                onDirectionChanged = {
                    haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                },
            )
            previous = current
        }
    }
}

@Composable
fun PostGridScrollHapticEffect(
    gridState: LazyGridState,
) {
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(gridState, haptic) {
        var previous = ScrollHapticSnapshot(
            index = gridState.firstVisibleItemIndex,
            offset = gridState.firstVisibleItemScrollOffset,
            isScrolling = gridState.isScrollInProgress,
        )
        var lastDirection = ScrollDirection.None

        snapshotFlow {
            ScrollHapticSnapshot(
                index = gridState.firstVisibleItemIndex,
                offset = gridState.firstVisibleItemScrollOffset,
                isScrolling = gridState.isScrollInProgress,
            )
        }.collect { current ->
            lastDirection = handleScrollHaptic(
                previous = previous,
                current = current,
                lastDirection = lastDirection,
                onDirectionChanged = {
                    haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                },
            )
            previous = current
        }
    }
}

private fun handleScrollHaptic(
    previous: ScrollHapticSnapshot,
    current: ScrollHapticSnapshot,
    lastDirection: ScrollDirection,
    onDirectionChanged: () -> Unit,
): ScrollDirection {
    if (!current.isScrolling) return ScrollDirection.None

    val currentDirection = when {
        current.index > previous.index -> ScrollDirection.Down
        current.index < previous.index -> ScrollDirection.Up
        current.offset > previous.offset -> ScrollDirection.Down
        current.offset < previous.offset -> ScrollDirection.Up
        else -> ScrollDirection.None
    }

    if (currentDirection != ScrollDirection.None && currentDirection != lastDirection) {
        onDirectionChanged()
        return currentDirection
    }

    return lastDirection
}

private data class ScrollHapticSnapshot(
    val index: Int,
    val offset: Int,
    val isScrolling: Boolean,
)

private enum class ScrollDirection {
    None,
    Up,
    Down,
}
