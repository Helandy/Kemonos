package su.afk.kemonos.profile.presenter.profile.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.api.model.Login

@Composable
internal fun SitePage(
    dateMode: DateFormatMode,
    title: String,
    isLoggedIn: Boolean,
    login: Login?,
    site: SelectedSite,
    updatedFavoritesCount: Int,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onFavoriteProfiles: () -> Unit,
    onFavoritePosts: () -> Unit,
    isExportInProgress: Boolean,
    isImportInProgress: Boolean,
    onExportFavoriteArtists: () -> Unit,
    onExportFavoritePosts: () -> Unit,
    onImportFavoriteArtists: () -> Unit,
    onImportFavoritePosts: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ExportFavoritesCard(
            enabled = isLoggedIn,
            blocked = isExportInProgress || isImportInProgress,
            inProgress = isExportInProgress,
            onExportArtists = onExportFavoriteArtists,
            onExportPosts = onExportFavoritePosts,
        )

        ImportFavoritesCard(
            enabled = isLoggedIn,
            blocked = isExportInProgress || isImportInProgress,
            inProgress = isImportInProgress,
            onImportArtists = onImportFavoriteArtists,
            onImportPosts = onImportFavoritePosts,
        )

        SiteAccountCard(
            dateMode = dateMode,
            title = title,
            isLoggedIn = isLoggedIn,
            login = login,
            onLoginClick = onLoginClick,
            onLogoutClick = onLogoutClick,
        )

        FavoritesCard(
            titleId = if (site == SelectedSite.C)
                R.string.profile_favorites_title_coomer
            else
                R.string.profile_favorites_title_kemono,
            enabled = isLoggedIn,
            onFavoriteProfiles = onFavoriteProfiles,
            onFavoritePosts = onFavoritePosts,
            updatesCount = updatedFavoritesCount
        )
    }
}
