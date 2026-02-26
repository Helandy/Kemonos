package su.afk.kemonos.creatorProfile.presenter.creatorProfile.view.discordProfile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DiscordProfilePlaceholder(
    onBack: () -> Unit
) {
    BaseScreen(
        isScroll = false,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.discord_profile_not_supported_title),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.discord_profile_not_supported_message),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            Button(onClick = onBack) {
                Text(stringResource(R.string.discord_profile_go_back))
            }
        }
    }
}