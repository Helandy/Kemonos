package su.afk.kemonos.posts.domain.pagingDms

import androidx.paging.PagingSource
import androidx.paging.PagingState
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.api.dms.DmDomain
import su.afk.kemonos.posts.domain.model.dms.DmsPageDomain
import su.afk.kemonos.posts.domain.repository.IPostsRepository

internal class DmsPagingSource(
    private val repository: IPostsRepository,
    private val site: SelectedSite,
    private val query: String?,
    private val pageSize: Int,
    private val forceRefresh: Boolean,
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
            forceRefresh = forceRefresh,
        )

        val items = page.dms
        val total = page.count
        val reachedEndByTotal = total != DmsPageDomain.UNKNOWN_COUNT && offset + items.size >= total
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
