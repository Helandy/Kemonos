package su.afk.kemonos.posts.domain.pagingDms

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.filter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.data.PostsRepository
import su.afk.kemonos.posts.domain.model.dms.DmDomain
import javax.inject.Inject

internal class GetDmsPagingUseCase @Inject constructor(
    private val repository: PostsRepository,
) {
    operator fun invoke(
        site: SelectedSite,
        query: String?,
    ): Flow<PagingData<DmDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                DmsPagingSource(
                    repository = repository,
                    site = site,
                    query = query,
                    pageSize = PAGE_SIZE,
                )
            }
        ).flow.map { pagingData ->
            val seen = HashSet<String>(PAGE_SIZE * 2)

            pagingData.filter { dm ->
                seen.add(dm.hash)
            }
        }
    }

    companion object {
        const val PAGE_SIZE = 50
    }
}
