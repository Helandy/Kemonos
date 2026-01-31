package su.afk.kemonos.creators.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.creators.data.ICreatorsRepository
import su.afk.kemonos.domain.models.creator.Creators
import su.afk.kemonos.domain.models.creator.CreatorsSort
import su.afk.kemonos.storage.api.IStoreCreatorsUseCase
import javax.inject.Inject

internal class GetCreatorsPagedUseCase @Inject constructor(
    private val store: IStoreCreatorsUseCase,
    private val repository: ICreatorsRepository
) {
    fun paging(
        service: String,
        query: String,
        sort: CreatorsSort,
        ascending: Boolean
    ): Flow<PagingData<Creators>> =
        Pager(
            config = PagingConfig(
                pageSize = 50,
                initialLoadSize = 100,
                prefetchDistance = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CreatorsPagingSource(
                    store = store,
                    service = service,
                    query = query,
                    sort = sort,
                    ascending = ascending,
                )
            }
        ).flow

    suspend fun getServices(): List<String> {
        val list = store.getDistinctServices()
        return listOf("Services") + list
    }

    suspend fun ensureFresh(): Boolean = repository.refreshCreatorsIfNeeded()

    suspend fun randomSuggestions(service: String, query: String, limit: Int): List<Creators> {
        return store.randomCreators(service, query, limit)
    }
}