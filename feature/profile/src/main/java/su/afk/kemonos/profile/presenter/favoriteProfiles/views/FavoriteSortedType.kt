package su.afk.kemonos.profile.presenter.favoriteProfiles.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import su.afk.kemonos.common.view.searchBar.SortOption
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.api.model.FavoriteArtist

/**
 *  дата новой публикации
 *  дата добавления в избранное
 *  дата реимпорта
 */
internal enum class FavoriteSortedType {
    NewPostsDate,
    FavedDate,
    ReimportDate,
}

@Composable
internal fun favoriteProfilesSortOptions(): List<SortOption<FavoriteSortedType>> = listOf(
    SortOption(
        type = FavoriteSortedType.NewPostsDate,
        label = stringResource(R.string.sort_new_posts_date)
    ),
    SortOption(
        type = FavoriteSortedType.FavedDate,
        label = stringResource(R.string.sort_faved_date)
    ),
    SortOption(
        type = FavoriteSortedType.ReimportDate,
        label = stringResource(R.string.sort_reimport_date)
    ),
)

internal fun FavoriteArtist.uiDateBySort(sortedType: FavoriteSortedType): String? {
    return when (sortedType) {
        FavoriteSortedType.NewPostsDate -> updated
        FavoriteSortedType.ReimportDate -> lastImported
        FavoriteSortedType.FavedDate -> null
    }
}