package su.afk.kemonos.creatorProfile.domain.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import su.afk.kemonos.creatorProfile.data.CreatorsRepository
import su.afk.kemonos.domain.models.PostDomain

internal class ProfilePostsPagingSource(
    private val repository: CreatorsRepository,
    private val service: String,
    private val id: String,
    private val tag: String?,
    private val search: String?,
    private val pageSize: Int,
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

            val items = repository.getProfilePosts(
                service = service,
                id = id,
                offset = offset,
                tag = tag,
                search = if (search.isNullOrEmpty()) null else search,
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
            LoadResult.Error(e)
        }
    }
}