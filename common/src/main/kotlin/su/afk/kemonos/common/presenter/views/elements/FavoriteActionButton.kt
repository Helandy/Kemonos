package su.afk.kemonos.common.presenter.views.elements

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import su.afk.kemonos.common.presenter.views.utilUI.favoriteColor

@Composable
fun FavoriteActionButton(
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
) {
    SmallFloatingActionButton(
        onClick = { onFavoriteClick() },
        containerColor = favoriteColor(isFavorite)
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorite) "In Favorite" else "Add to Favorite",
            tint = if (isFavorite) MaterialTheme.colorScheme.onSecondaryContainer
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}