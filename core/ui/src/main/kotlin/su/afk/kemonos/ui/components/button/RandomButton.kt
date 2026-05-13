package su.afk.kemonos.ui.components.button

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.motion.KemonosMotion

@Composable
fun RandomButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val alpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.5f,
        animationSpec = KemonosMotion.pressScaleSpec,
        label = "randomButtonAlpha",
    )

    FloatingActionButton(
        onClick = { if (enabled) onClick() },
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .animateContentSize()
            .alpha(alpha)
            .clickable(enabled = enabled) {},
        containerColor = MaterialTheme.colorScheme.primary,
    ) {
        Icon(
            imageVector = Icons.Filled.Casino,
            contentDescription = stringResource(R.string.random),
            modifier = Modifier
                .padding(8.dp)
                .size(48.dp)
        )
    }
}
