package su.afk.kemonos.creatorProfile.api

import androidx.navigation3.runtime.NavKey

interface ICreatorProfileNavigator {

    fun getCreatorProfileDest(
        service: String,
        id: String,
    ): NavKey
}