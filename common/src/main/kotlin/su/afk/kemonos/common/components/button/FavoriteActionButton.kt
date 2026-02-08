package su.afk.kemonos.common.components.button

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.utilsUI.favoriteColor

@Composable
fun FavoriteActionButton(
    enabled: Boolean,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
) {
    SmallFloatingActionButton(
        onClick = { if (enabled) onFavoriteClick() },
        containerColor = favoriteColor(isFavorite)
    ) {
        if (!enabled) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(4.dp)
            )
        } else {
            Icon(
                imageVector = if (isFavorite)
                    Icons.Filled.Favorite
                else
                    Icons.Outlined.FavoriteBorder,
                contentDescription = if (isFavorite)
                    "In Favorite"
                else
                    "Add to Favorite",
                tint = if (isFavorite)
                    MaterialTheme.colorScheme.onSecondaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}