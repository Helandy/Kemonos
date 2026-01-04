package su.afk.kemonos.common.shared.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable

@Composable
fun SharedActionButton(
    onClick: () -> Unit,
) {
    SmallFloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Icon(
            imageVector = Icons.Filled.Share,
            contentDescription = "Share",
        )
    }
}