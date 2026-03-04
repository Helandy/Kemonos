package su.afk.kemonos.posts.presenter.pager.model

import androidx.compose.runtime.Composable
import su.afk.kemonos.posts.presenter.pageDm.DmNavigation
import su.afk.kemonos.posts.presenter.pageHashLookup.HashLookupNavigation
import su.afk.kemonos.posts.presenter.pagePopularPosts.PopularPostsNavigation
import su.afk.kemonos.posts.presenter.pageSearchPosts.SearchPostsNavigation
import su.afk.kemonos.posts.presenter.pageTags.TagsPageNavigation

internal data class PostsPageDescriptor(
    val page: PostsPage,
    val saveKey: String,
    val content: @Composable () -> Unit,
)

internal val POSTS_PAGE_DESCRIPTORS: List<PostsPageDescriptor> = listOf(
    PostsPageDescriptor(
        page = PostsPage.HashLookup,
        saveKey = "posts_page_hash_lookup",
        content = { HashLookupNavigation() },
    ),
    PostsPageDescriptor(
        page = PostsPage.Search,
        saveKey = "posts_page_search",
        content = { SearchPostsNavigation() },
    ),
    PostsPageDescriptor(
        page = PostsPage.Popular,
        saveKey = "posts_page_popular",
        content = { PopularPostsNavigation() },
    ),
    PostsPageDescriptor(
        page = PostsPage.Dm,
        saveKey = "posts_page_dm",
        content = { DmNavigation() },
    ),
    PostsPageDescriptor(
        page = PostsPage.Tags,
        saveKey = "posts_page_tags",
        content = { TagsPageNavigation() },
    ),
)

internal val POSTS_PAGES: List<PostsPage> = POSTS_PAGE_DESCRIPTORS.map { it.page }

internal val FALLBACK_POSTS_PAGE_DESCRIPTOR: PostsPageDescriptor =
    POSTS_PAGE_DESCRIPTORS.find { it.page == PostsPage.Popular } ?: POSTS_PAGE_DESCRIPTORS.first()

internal fun List<PostsPage>.indexOfOrPopular(page: PostsPage): Int {
    val pageIndex = indexOf(page)
    if (pageIndex >= 0) return pageIndex

    return indexOf(PostsPage.Popular).takeIf { it >= 0 } ?: 0
}
