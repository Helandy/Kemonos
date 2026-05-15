package su.afk.kemonos.ui.haptic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
fun rememberPullRefreshWithHaptic(
    enabled: Boolean = true,
    onRefresh: () -> Unit,
): () -> Unit {
    val haptic = LocalHapticFeedback.current
    return remember(enabled, haptic, onRefresh) {
        {
            if (enabled) {
                haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
            }
            onRefresh()
        }
    }
}
