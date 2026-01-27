package su.afk.kemonos.profile.presenter.profile.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.utilsUI.KemonosPreviewScreen
import su.afk.kemonos.profile.R

/** Избранное */
@Composable
internal fun FavoritesCard(
    titleId: Int,
    updatesCount: Int,
    onFavoriteProfiles: () -> Unit,
    onFavoritePosts: () -> Unit,
) {
    Text(
        text = stringResource(titleId),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Medium
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.clickable { onFavoriteProfiles() }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.profile_favorites_profiles),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                if (updatesCount > 0) {
                    Badge(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 6.dp, y = (-6).dp),
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ) {
                        Text(
                            text = updatesCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.clickable { onFavoritePosts() }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.profile_favorites_posts),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview("PreviewFavoritesCard")
@Composable
private fun PreviewFavoritesCard() {
    KemonosPreviewScreen {
        FavoritesCard(
            titleId = R.string.profile_favorites_title_coomer,
            updatesCount = 1,
            onFavoriteProfiles = {},
            onFavoritePosts = {},
        )
    }
}