package su.afk.kemonos.creatorProfile.navigation

import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import javax.inject.Inject

class CreatorProfileNavigator @Inject constructor() : ICreatorProfileNavigator {

    override fun getCreatorProfileDest(
        service: String,
        id: String,
    ): NavKey {
        return CreatorDest.CreatorProfile(
            service = service,
            id = id,
        )
    }
}