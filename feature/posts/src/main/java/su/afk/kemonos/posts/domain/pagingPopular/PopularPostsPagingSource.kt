package su.afk.kemonos.posts.domain.pagingPopular

import androidx.paging.PagingSource
import androidx.paging.PagingState
import su.afk.kemonos.api.domain.popular.PopularPosts
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.posts.data.PostsRepository
import su.afk.kemonos.posts.domain.model.popular.Period

internal class PopularPostsPagingSource(
    private val repository: PostsRepository,
    private val site: SelectedSite,
    private val date: String?,
    private val period: Period,
    private val pageSize: Int,
    private val onMeta: (PopularPosts) -> Unit,
) : PagingSource<Int, PostDomain>() {

    override fun getRefreshKey(state: PagingState<Int, PostDomain>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostDomain> = try {
        val pageIndex = params.key ?: 0
        val offset = pageIndex * pageSize

        val popular = repository.getPopularPosts(
            site = site,
            date = date,
            period = period,
            offset = offset,
        )

        if (pageIndex == 0) {
            /** сюда прилетит navigationDates */
            onMeta(popular)
        }

        val items = popular.posts

        /** конец понимаем по total count (лучше чем items.size < pageSize) */
        val total = popular.props.count
        val nextKey = if (offset + items.size >= total) null else pageIndex + 1

        LoadResult.Page(
            data = items,
            prevKey = if (pageIndex == 0) null else pageIndex - 1,
            nextKey = nextKey,
        )
    } catch (t: Throwable) {
        LoadResult.Error(t)
    }
}
