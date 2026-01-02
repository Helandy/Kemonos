package su.afk.kemonos.creators.domain

import androidx.paging.PagingSource
import androidx.paging.PagingState
import su.afk.kemonos.domain.models.Creators
import su.afk.kemonos.domain.models.CreatorsSort
import su.afk.kemonos.storage.api.StoreCreatorsUseCase

internal class CreatorsPagingSource(
    private val store: StoreCreatorsUseCase,
    private val service: String,
    private val query: String,
    private val sort: CreatorsSort,
    private val ascending: Boolean,
) : PagingSource<Int, Creators>() {

    override fun getRefreshKey(state: PagingState<Int, Creators>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(page.data.size) ?: page.nextKey?.minus(page.data.size)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Creators> {
        return try {
            val offset = params.key ?: 0
            val limit = params.loadSize.coerceAtMost(50)

            val data = store.searchCreators(
                service = service,
                query = query,
                sort = sort,
                ascending = ascending,
                limit = limit,
                offset = offset,
            )

            val nextKey = if (data.size < limit) null else offset + data.size
            val prevKey = if (offset == 0) null else (offset - limit).coerceAtLeast(0)

            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }
}