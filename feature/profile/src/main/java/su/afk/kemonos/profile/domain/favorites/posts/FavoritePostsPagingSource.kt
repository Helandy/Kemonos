package su.afk.kemonos.profile.domain.favorites.posts

import androidx.paging.PagingSource
import androidx.paging.PagingState
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.profile.data.IFavoritesRepository

internal class FavoritePostsPagingSource(
    private val repository: IFavoritesRepository,
    private val site: SelectedSite,
    private val query: String?,
) : PagingSource<Int, PostDomain>() {

    override fun getRefreshKey(state: PagingState<Int, PostDomain>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(page.data.size) ?: page.nextKey?.minus(page.data.size)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostDomain> {
        return try {
            val offset = params.key ?: 0
            val limit = params.loadSize.coerceAtMost(50)

            val data = repository.pageFavoritePosts(
                site = site,
                query = query,
                limit = limit,
                offset = offset
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
