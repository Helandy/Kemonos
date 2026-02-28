package su.afk.kemonos.ui.shared.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.uiUtils.size.formatBytes

@Composable
fun ShareLoadingOverlay(
    visible: Boolean,
    bytesRead: Long = 0L,
    totalBytes: Long = 0L,
    modifier: Modifier = Modifier,
) {
    if (!visible) return

    val hasProgress = bytesRead > 0L || totalBytes > 0L
    val hasKnownTotal = totalBytes > 0L
    val progress = if (hasKnownTotal) {
        (bytesRead.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    val text = if (hasProgress) {
        if (hasKnownTotal) {
            "${formatBytes(bytesRead)} / ${formatBytes(totalBytes)}"
        } else {
            formatBytes(bytesRead)
        }
    } else {
        stringResource(R.string.loading)
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            tonalElevation = 6.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (hasKnownTotal) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 3.dp
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 3.dp
                    )
                }
                Spacer(Modifier.size(12.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
