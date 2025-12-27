package su.afk.kemonos.profile.api.domain

import androidx.navigation3.runtime.NavKey

interface IProfileNavigator {
    fun getProfileDest(): NavKey
}