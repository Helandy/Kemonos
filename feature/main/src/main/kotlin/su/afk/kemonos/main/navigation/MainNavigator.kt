package su.afk.kemonos.main.navigation

import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.main.api.IMainNavigator
import javax.inject.Inject

class MainNavigator @Inject constructor() : IMainNavigator {
    override fun getMainDest(): NavKey = MainDest
}