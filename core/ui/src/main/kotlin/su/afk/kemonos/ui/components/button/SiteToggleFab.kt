package su.afk.kemonos.ui.components.button

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.ui.R

@Composable
fun SiteToggleFab(
    enable: Boolean,
    selectedSite: SelectedSite,
    onToggleSite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = { if (enable) onToggleSite() },
        modifier = modifier
            .alpha(if (enable) 1f else 0.5f)
            .clickable(enabled = enable) {},
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Icon(
            painter = painterResource(
                id = when (selectedSite) {
                    SelectedSite.K -> R.drawable.kemono_logo
                    SelectedSite.C -> R.drawable.coomer_logo
                }
            ),
            modifier = Modifier
                .padding(8.dp)
                .size(48.dp),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}

