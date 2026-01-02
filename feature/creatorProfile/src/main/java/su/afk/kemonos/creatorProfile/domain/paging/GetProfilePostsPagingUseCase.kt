package su.afk.kemonos.creatorProfile.domain.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.creatorProfile.data.CreatorsRepository
import su.afk.kemonos.domain.models.PostDomain
import javax.inject.Inject

internal class GetProfilePostsPagingUseCase @Inject constructor(
    private val repository: CreatorsRepository
) {
    operator fun invoke(
        service: String,
        id: String,
        tag: String?,
        search: String?,
    ): Flow<PagingData<PostDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ProfilePostsPagingSource(
                    repository = repository,
                    service = service,
                    id = id,
                    tag = tag,
                    search = search,
                    pageSize = PAGE_SIZE,
                )
            }
        ).flow
    }

    companion object {
        const val PAGE_SIZE = 50
    }
}