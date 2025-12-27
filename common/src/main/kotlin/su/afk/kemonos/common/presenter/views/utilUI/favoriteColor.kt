package su.afk.kemonos.common.presenter.views.utilUI

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun favoriteColor(isFavorite: Boolean): Color {
    return if (isFavorite)
        MaterialTheme.colorScheme.secondaryContainer
    else
        MaterialTheme.colorScheme.surfaceVariant
}