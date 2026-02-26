package su.afk.kemonos.creatorProfile.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import su.afk.kemonos.domain.models.Tag

object CreatorDest {

    @Serializable
    data class CreatorProfile(
        val service: String,
        val id: String,
        val tag: Tag? = null
    ) : NavKey

    @Serializable
    data class CommunityChat(
        val service: String,
        val creatorId: String,
        val channelId: String,
        val channelName: String
    ) : NavKey
}
