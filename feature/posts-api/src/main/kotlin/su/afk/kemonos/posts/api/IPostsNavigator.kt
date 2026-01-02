package su.afk.kemonos.posts.api

import androidx.navigation3.runtime.NavKey

interface IPostsNavigator {
    fun getPostsDest(): NavKey
}