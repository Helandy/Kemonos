package su.afk.kemonos.profile.navigation

import androidx.navigation3.runtime.NavKey

internal object AuthDestination {
    object Profile : NavKey

    object Login : NavKey

    object Register : NavKey

    object FavoriteProfiles : NavKey

    object FavoritePosts : NavKey

    object AuthorsBlacklist : NavKey

    object Faq : NavKey
}
