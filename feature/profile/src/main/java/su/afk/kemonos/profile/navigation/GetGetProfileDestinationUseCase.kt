package su.afk.kemonos.profile.navigation

import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.profile.api.domain.IGetProfileDestinationUseCase

class GetGetProfileDestinationUseCase @Inject constructor() : IGetProfileDestinationUseCase {
    override fun getProfileDest(): NavKey = AuthDestination.Profile
}