package su.afk.kemonos.creatorPost.api

import androidx.navigation3.runtime.NavKey

interface ICreatorPostNavigator {
    fun getCreatorPostDest(
        id: String,
        service: String,
        postId: String,
        showBarCreator: Boolean
    ): NavKey
}