package su.afk.kemonos.creatorProfile.navigation

import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.domain.models.Tag
import javax.inject.Inject

class CreatorProfileNavigator @Inject constructor() : ICreatorProfileNavigator {

    override suspend fun getCreatorProfileDest(
        service: String,
        id: String,
        tag: Tag?,
    ): NavKey {
        return CreatorDestination.CreatorProfile(
            service = service,
            id = id,
            tag = tag,
        )
    }
}
