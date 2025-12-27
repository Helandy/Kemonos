package su.afk.kemonos.profile.navigation

import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.profile.api.domain.IProfileNavigator

class ProfileNavigator @Inject constructor() : IProfileNavigator {
    override fun getProfileDest(): NavKey = AuthDest.Profile
}