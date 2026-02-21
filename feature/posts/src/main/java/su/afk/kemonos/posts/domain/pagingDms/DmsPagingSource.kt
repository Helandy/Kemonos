package su.afk.kemonos.posts.domain.pagingDms

import androidx.paging.PagingSource
import androidx.paging.PagingState
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.data.PostsRepository
import su.afk.kemonos.posts.domain.model.dms.DmDomain

internal class DmsPagingSource(
    private val repository: PostsRepository,
    private val site: SelectedSite,
    private val query: String?,
    private val pageSize: Int,
) : PagingSource<Int, DmDomain>() {

    override fun getRefreshKey(state: PagingState<Int, DmDomain>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DmDomain> = try {
        val pageIndex = params.key ?: 0
        val offset = pageIndex * pageSize

        val page = repository.getDms(
            site = site,
            offset = offset,
            limit = pageSize,
            query = query,
        )

        val items = page.dms
        val total = page.count
        val reachedEndByTotal = offset + items.size >= total
        val reachedEndByBatch = items.size < pageSize
        val nextKey = if (reachedEndByTotal || reachedEndByBatch) null else pageIndex + 1

        LoadResult.Page(
            data = items,
            prevKey = if (pageIndex == 0) null else pageIndex - 1,
            nextKey = nextKey,
        )
    } catch (t: Throwable) {
        LoadResult.Error(t)
    }
}
