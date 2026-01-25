package su.afk.kemonos.creators.presenter.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun CreatorsSectionHeader(
    title: String,
    topSpace: Dp = 0.dp,
    showTopDivider: Boolean = false,
    showBottomDivider: Boolean = true
) {
    Column(Modifier.fillMaxWidth()) {
        if (showTopDivider) HorizontalDivider()
        if (topSpace > 0.dp) Spacer(Modifier.height(topSpace))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            textAlign = TextAlign.Center
        )

        if (showBottomDivider) HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}