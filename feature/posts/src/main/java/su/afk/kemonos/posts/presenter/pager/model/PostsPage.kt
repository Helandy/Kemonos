package su.afk.kemonos.posts.presenter.pager.model

import androidx.annotation.StringRes
import su.afk.kemonos.posts.R

internal sealed class PostsPage(
    @StringRes val titleRes: Int,
) {
    data object Search : PostsPage(R.string.posts_tab_search)
    data object Popular : PostsPage(R.string.posts_tab_popular)
    data object Tags : PostsPage(R.string.posts_tab_tags)
}

internal val ALL_POSTS_PAGES: List<PostsPage> = listOf(
    PostsPage.Search,
    PostsPage.Popular,
    PostsPage.Tags,
)