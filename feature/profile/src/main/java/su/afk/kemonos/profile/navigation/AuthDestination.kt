package su.afk.kemonos.profile.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

internal object AuthDestination {
    @Serializable
    data object Profile : NavKey

    @Serializable
    data object Login : NavKey

    @Serializable
    data object Register : NavKey

    @Serializable
    data object FavoriteProfiles : NavKey

    @Serializable
    data object FavoritePosts : NavKey

    @Serializable
    data object AuthorsBlacklist : NavKey

    @Serializable
    data object Faq : NavKey

    @Serializable
    data object ImportResult : NavKey
}
