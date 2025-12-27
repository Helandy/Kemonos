package su.afk.kemonos.creators.navigation

import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.creators.ICreatorsNavigator
import javax.inject.Inject

class CreatorsNavigator @Inject constructor() : ICreatorsNavigator {
    override fun getCreatorsDest(): NavKey = CreatorsDest
}