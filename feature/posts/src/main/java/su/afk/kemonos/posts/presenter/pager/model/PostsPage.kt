package su.afk.kemonos.posts.presenter.pager.model

import kotlinx.serialization.Serializable
import su.afk.kemonos.posts.R
import su.afk.kemonos.ui.R as CommonR

@Serializable
internal sealed class PostsPage(
    @get:androidx.annotation.StringRes
    val titleRes: Int,
) {

    @Serializable
    data object HashLookup : PostsPage(R.string.posts_tab_hash_lookup)

    @Serializable
    data object Search : PostsPage(CommonR.string.search)

    @Serializable
    data object Popular : PostsPage(R.string.posts_tab_popular)

    @Serializable
    data object Dm : PostsPage(R.string.posts_tab_dm)

    @Serializable
    data object Tags : PostsPage(CommonR.string.tags)
}
