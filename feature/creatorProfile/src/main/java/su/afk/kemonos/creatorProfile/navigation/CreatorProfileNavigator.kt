package su.afk.kemonos.creatorProfile.navigation

import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.creatorProfile.util.Utils.queryKey
import su.afk.kemonos.storage.api.profilePosts.IStorageCreatorPostsCacheUseCase
import javax.inject.Inject

class CreatorProfileNavigator @Inject constructor(
    private val postsCache: IStorageCreatorPostsCacheUseCase,
) : ICreatorProfileNavigator {

    override suspend fun getCreatorProfileDest(
        service: String,
        id: String,
        isFresh: Boolean
    ): NavKey {
        if (isFresh) {
            val qk = queryKey(
                service = service,
                id = id,
                search = "",
                tag = null,
            )
            postsCache.clearQuery(qk)
        }

        return CreatorDest.CreatorProfile(
            service = service,
            id = id,
        )
    }
}