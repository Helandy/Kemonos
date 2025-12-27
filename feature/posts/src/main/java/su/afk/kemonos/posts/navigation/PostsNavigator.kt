package su.afk.kemonos.posts.navigation

import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.api.IPostsNavigator
import javax.inject.Inject

class PostsNavigator @Inject constructor() : IPostsNavigator {
    override fun getPostsDest(): NavKey = PostsDest.PostsPager
}