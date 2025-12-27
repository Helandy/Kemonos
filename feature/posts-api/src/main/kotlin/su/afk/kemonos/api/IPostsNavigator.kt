package su.afk.kemonos.api

import androidx.navigation3.runtime.NavKey

interface IPostsNavigator {

    fun getPostsDest(): NavKey
}