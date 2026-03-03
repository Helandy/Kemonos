package su.afk.kemonos.profile.api.domain

import androidx.navigation3.runtime.NavKey

interface IGetProfileDestinationUseCase {
    fun getProfileDest(): NavKey
}