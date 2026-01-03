package su.afk.kemonos.profile.presenter.profile.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.api.model.Login

@Composable
internal fun SitePage(
    title: String,
    isLoggedIn: Boolean,
    login: Login?,
    site: SelectedSite,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onFavoriteProfiles: () -> Unit,
    onFavoritePosts: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SiteAccountCard(
            title = title,
            isLoggedIn = isLoggedIn,
            login = login,
            onLoginClick = onLoginClick,
            onLogoutClick = onLogoutClick,
        )

        if (isLoggedIn) {
            FavoritesCard(
                titleId = if (site == SelectedSite.C)
                    R.string.profile_favorites_title_coomer
                else
                    R.string.profile_favorites_title_kemono,
                onFavoriteProfiles = onFavoriteProfiles,
                onFavoritePosts = onFavoritePosts
            )
        }
    }
}
