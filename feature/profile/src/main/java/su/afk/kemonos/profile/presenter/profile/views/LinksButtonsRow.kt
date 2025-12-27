package su.afk.kemonos.profile.presenter.profile.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
internal fun LinksButtonsRow(
    kemonoUrl: String,
    coomerUrl: String,
) {
    val uriHandler = LocalUriHandler.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = { if (kemonoUrl.isNotBlank()) uriHandler.openUri(kemonoUrl) },
            modifier = Modifier.weight(1f),
            enabled = kemonoUrl.isNotBlank()
        ) { Text("Kemono", maxLines = 1, overflow = TextOverflow.Ellipsis) }

        OutlinedButton(
            onClick = { if (coomerUrl.isNotBlank()) uriHandler.openUri(coomerUrl) },
            modifier = Modifier.weight(1f),
            enabled = coomerUrl.isNotBlank()
        ) { Text("Coomer", maxLines = 1, overflow = TextOverflow.Ellipsis) }
    }
}