package su.afk.kemonos.posts.presenter.pager.model

import androidx.annotation.StringRes
import su.afk.kemonos.posts.R
import su.afk.kemonos.ui.R as CommonR

internal sealed class PostsPage(
    @StringRes val titleRes: Int,
) {
    data object Search : PostsPage(CommonR.string.search)
    data object Dm : PostsPage(R.string.posts_tab_dm)
    data object Popular : PostsPage(R.string.posts_tab_popular)
    data object Tags : PostsPage(CommonR.string.tags)
    data object HashLookup : PostsPage(R.string.posts_tab_hash_lookup)
}

internal val ALL_POSTS_PAGES: List<PostsPage> = listOf(
    PostsPage.Search,
    PostsPage.Dm,
    PostsPage.Popular,
    PostsPage.Tags,
    PostsPage.HashLookup,
)
