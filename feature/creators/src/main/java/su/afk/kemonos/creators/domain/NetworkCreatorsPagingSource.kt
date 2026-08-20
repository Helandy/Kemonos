package su.afk.kemonos.creators.domain

import androidx.paging.PagingSource
import androidx.paging.PagingState
import su.afk.kemonos.creators.domain.repository.ICreatorsRepository
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.Creators
import su.afk.kemonos.domain.models.creator.CreatorsSort

/**
 * Пейджинг авторов прямо из сети.
 *
 * Используется источниками, которые не отдают список целиком: поиск, фильтр
 * и сортировка уходят на сервер, локальный кэш не участвует.
 */
internal class NetworkCreatorsPagingSource(
    private val site: SelectedSite,
    private val repository: ICreatorsRepository,
    private val service: String?,
    private val query: String,
    private val sort: CreatorsSort,
) : PagingSource<Int, Creators>() {

    override fun getRefreshKey(state: PagingState<Int, Creators>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(page.data.size) ?: page.nextKey?.minus(page.data.size)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Creators> {
        return try {
            val offset = params.key ?: 0
            /** Потолок страницы у OnlyHaven — 50. */
            val limit = params.loadSize.coerceAtMost(MAX_PAGE_SIZE)

            val data = repository.getCreatorsPage(
                site = site,
                service = service,
                query = query,
                sort = sort,
                limit = limit,
                offset = offset,
            )

            LoadResult.Page(
                data = data,
                prevKey = if (offset == 0) null else (offset - limit).coerceAtLeast(0),
                nextKey = if (data.size < limit) null else offset + data.size,
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }

    private companion object {
        const val MAX_PAGE_SIZE = 50
    }
}
