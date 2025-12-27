package su.afk.kemonos.creatorPost.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object CreatorPostDest {
    @Serializable
    data class CreatorPost(
        val id: String,
        val service: String,
        val postId: String,
        val showBarCreator: Boolean
    ) : NavKey
}