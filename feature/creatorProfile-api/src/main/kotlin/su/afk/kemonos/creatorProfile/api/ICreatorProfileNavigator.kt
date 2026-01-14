package su.afk.kemonos.creatorProfile.api

import androidx.navigation3.runtime.NavKey

interface ICreatorProfileNavigator {

    suspend fun getCreatorProfileDest(
        service: String,
        id: String,
        isFresh: Boolean = false,
    ): NavKey
}