package su.afk.kemonos.posts.domain.pagingSearch

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CancellationException
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.posts.domain.repository.IPostsRepository

internal class SearchPostsPagingSource(
    private val repository: IPostsRepository,
    private val site: SelectedSite,
    private val tag: String?,
    private val search: String?,
    private val pageSize: Int,
    private val forceRefresh: Boolean,
) : PagingSource<Int, PostDomain>() {

    override fun getRefreshKey(state: PagingState<Int, PostDomain>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostDomain> {
        return try {
            val pageIndex = params.key ?: 0
            val limit = pageSize
            val offset = pageIndex * limit

            val items = repository.getPosts(
                site = site,
                offset = offset,
                tag = tag,
                query = search,
                forceRefresh = forceRefresh,
            )

            val nextKey = if (items.size < limit) {
                null
            } else {
                pageIndex + 1
            }

            LoadResult.Page(
                data = items,
                prevKey = if (pageIndex == 0) null else pageIndex - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            LoadResult.Error(e)
        }
    }
}
