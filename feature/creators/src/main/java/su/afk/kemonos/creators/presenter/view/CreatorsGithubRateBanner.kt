package su.afk.kemonos.creators.presenter.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.ui.R

@Composable
internal fun CreatorsGithubRateBanner(
    visible: Boolean,
    onRateClick: () -> Unit,
    onNeverShowClick: () -> Unit,
) {
    if (!visible) return

    ElevatedCard(modifier = Modifier.padding(top = 8.dp)) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = stringResource(R.string.main_rate_banner_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.main_rate_banner_subtitle),
                style = MaterialTheme.typography.bodyMedium,
            )
            Row {
                TextButton(onClick = onNeverShowClick) {
                    Text(stringResource(R.string.main_rate_banner_never_show))
                }
                Spacer(Modifier.weight(1f))
                Button(onClick = onRateClick) {
                    Text(stringResource(R.string.main_rate_banner_rate))
                }
            }
        }
    }
}