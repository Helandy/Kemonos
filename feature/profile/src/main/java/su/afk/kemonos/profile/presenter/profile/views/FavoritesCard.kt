package su.afk.kemonos.profile.presenter.profile.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.profile.R
import su.afk.kemonos.ui.preview.KemonosPreviewScreen

/** Избранное */
@Composable
internal fun FavoritesCard(
    titleId: Int,
    updatesCount: Int,
    enabled: Boolean,
    onFavoriteProfiles: () -> Unit,
    onFavoritePosts: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(titleId),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        ProfileActionCard(
            title = stringResource(R.string.profile_favorites_profiles),
            icon = Icons.Filled.Groups,
            badgeCount = updatesCount,
            enabled = enabled,
            onClick = onFavoriteProfiles,
        )

        ProfileActionCard(
            title = stringResource(R.string.profile_favorites_posts),
            icon = Icons.Filled.Dashboard,
            enabled = enabled,
            onClick = onFavoritePosts,
        )
    }
}

@Preview("PreviewFavoritesCard")
@Composable
private fun PreviewFavoritesCard() {
    KemonosPreviewScreen {
        FavoritesCard(
            titleId = R.string.profile_favorites_title_coomer,
            updatesCount = 1,
            enabled = true,
            onFavoriteProfiles = {},
            onFavoritePosts = {},
        )
    }
}
