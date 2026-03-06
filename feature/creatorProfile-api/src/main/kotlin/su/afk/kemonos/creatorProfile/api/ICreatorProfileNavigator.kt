package su.afk.kemonos.creatorProfile.api

import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.domain.models.Tag

interface ICreatorProfileNavigator {

    suspend fun getCreatorProfileDest(
        service: String,
        id: String,
        tag: Tag? = null,
    ): NavKey

    suspend fun getCommunityChatDest(
        service: String,
        creatorId: String,
        channelId: String,
        channelName: String = channelId,
    ): NavKey
}
