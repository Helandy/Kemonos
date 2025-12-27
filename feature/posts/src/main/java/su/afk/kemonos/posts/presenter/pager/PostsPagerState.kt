package su.afk.kemonos.posts.presenter.pager

import su.afk.kemonos.posts.presenter.pager.model.PostsPage

internal data class PostsPagerState(
    val currentPage: PostsPage = PostsPage.Popular,
)