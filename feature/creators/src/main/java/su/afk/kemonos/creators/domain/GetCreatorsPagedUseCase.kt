package su.afk.kemonos.creators.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.common.data.creators.CreatorsDto.Companion.toDomain
import su.afk.kemonos.core.api.domain.net.helpers.call
import su.afk.kemonos.creators.data.api.CreatorsApi
import su.afk.kemonos.domain.domain.models.Creators
import su.afk.kemonos.domain.domain.models.CreatorsSort
import su.afk.kemonos.storage.api.StoreCreatorsUseCase
import javax.inject.Inject

internal class GetCreatorsPagedUseCase @Inject constructor(
    private val store: StoreCreatorsUseCase,
    private val api: CreatorsApi,
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
        return listOf("All") + list
    }

    suspend fun ensureFresh(): Boolean {
        if (store.isCreatorsCacheFresh()) return false

        val fromNet = api.getCreators().call { list -> list.map { it.toDomain() } }
        if (fromNet.isEmpty()) return false

        store.updateCreators(fromNet)
        return true
    }
}