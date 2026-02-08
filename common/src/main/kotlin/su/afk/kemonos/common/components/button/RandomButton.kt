package su.afk.kemonos.common.components.button

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R

@Composable
fun RandomButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SmallFloatingActionButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
            .clickable(enabled = enabled) {},
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
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