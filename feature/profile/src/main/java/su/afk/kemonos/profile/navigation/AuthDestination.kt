package su.afk.kemonos.profile.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import su.afk.kemonos.domain.SelectedSite

internal object AuthDestination {
    @Serializable
    data object Profile : NavKey

    @Serializable
    data class Login(val site: SelectedSite) : NavKey

    @Serializable
    data class Register(val site: SelectedSite) : NavKey

    @Serializable
    data class FavoriteProfiles(val site: SelectedSite) : NavKey

    @Serializable
    data class FavoritePosts(val site: SelectedSite) : NavKey

    @Serializable
    data object AuthorsBlacklist : NavKey

    @Serializable
    data object Faq : NavKey

    @Serializable
    data object ImportResult : NavKey
}
