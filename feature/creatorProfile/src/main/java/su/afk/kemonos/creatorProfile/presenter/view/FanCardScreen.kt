package su.afk.kemonos.creatorProfile.presenter.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.common.presenter.views.imageLoader.AsyncImageWithStatus
import su.afk.kemonos.common.util.toUiDateTime
import su.afk.kemonos.creatorProfile.api.domain.models.profileFanCards.ProfileFanCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FanCardGridScreen(
    fanCards: List<ProfileFanCard>,
    onCardClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (fanCards.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.fancard_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(fanCards) { fanCard ->
                FanCardItem(
                    fanCard = fanCard,
                    onClick = {
                        onCardClick(
                            "${fanCard.server}${fanCard.path}"
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun FanCardItem(
    fanCard: ProfileFanCard,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(bottom = 12.dp)
    ) {
        AsyncImageWithStatus(
            model = "${fanCard.server}${fanCard.path}",
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            text = stringResource(R.string.fancard_size, (fanCard.size / 1024).toString()),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = stringResource(R.string.fancard_added, fanCard.added.toUiDateTime()),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}