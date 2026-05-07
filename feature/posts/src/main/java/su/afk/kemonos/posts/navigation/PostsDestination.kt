package su.afk.kemonos.posts.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object PostsDestination {
    @Serializable
    data object PostsPager : NavKey

    @Serializable
    data object TagsSelect : NavKey
}
