package su.afk.kemonos.profile.presenter.profile.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import su.afk.kemonos.profile.R

/** Избранное */
@Composable
internal fun FavoritesCard(
    titleId: Int,
    onFavoriteProfiles: () -> Unit,
    onFavoritePosts: () -> Unit,
) {
    Text(
        text = stringResource(titleId),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Medium
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onFavoriteProfiles() }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = stringResource(R.string.profile_favorites_profiles))
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onFavoritePosts() }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.profile_favorites_posts))
            }
        }
    }
}