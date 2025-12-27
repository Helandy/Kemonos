package su.afk.kemonos.creatorPost.navigation

import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.creatorPost.api.ICreatorPostNavigator
import javax.inject.Inject

class CreatorPostDestNavigator @Inject constructor() : ICreatorPostNavigator {

    override fun getCreatorPostDest(id: String, service: String, postId: String, showBarCreator: Boolean): NavKey {
        return CreatorPostDest.CreatorPost(
            id = id,
            service = service,
            postId = postId,
            showBarCreator = showBarCreator
        )
    }
}