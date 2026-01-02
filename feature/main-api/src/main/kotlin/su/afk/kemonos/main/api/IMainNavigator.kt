package su.afk.kemonos.main.api

import androidx.navigation3.runtime.NavKey

interface IMainNavigator {
    fun getMainDest(): NavKey
}