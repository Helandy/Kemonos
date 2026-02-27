package su.afk.kemonos.creatorProfile.presenter.creatorProfile.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityChannel
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.date.toUiDateTime

@Composable
internal fun CommunityScreen(
    dateMode: DateFormatMode,
    channels: List<CommunityChannel>,
    onOpenChannel: (CommunityChannel) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(channels, key = { it.channelId }) { channel ->
            Card(
                onClick = { onOpenChannel(channel) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = channel.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(12.dp)
                )

                channel.messagesRefreshedAt
                    ?.takeIf { it.isNotBlank() }
                    ?.let { refreshedAt ->
                        Text(
                            text = stringResource(
                                R.string.profile_community_messages_refreshed_at,
                                refreshedAt.toUiDateTime(dateMode)
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                        )
                    }
            }
        }
    }
}
