package su.afk.kemonos.creators.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.creators.data.ICreatorsRepository
import su.afk.kemonos.domain.models.creator.Creators
import su.afk.kemonos.domain.models.creator.CreatorsSort
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.storage.api.repository.creators.IStoreCreatorsRepository
import javax.inject.Inject

internal class GetCreatorsPagedUseCase @Inject constructor(
    private val selectedSite: ISelectedSiteUseCase,
    private val store: IStoreCreatorsRepository,
    private val repository: ICreatorsRepository
) {
    /** Проверка свежий ли кэш */
    suspend fun checkFreshCache(): Boolean = repository.refreshCreatorsIfNeeded()

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
                    site = selectedSite.getSite(),
                    store = store,
                    service = service,
                    query = query,
                    sort = sort,
                    ascending = ascending,
                )
            }
        ).flow

    suspend fun getServices(): List<String> {
        val list = store.getDistinctServices(site = selectedSite.getSite())
        return listOf("Services") + list
    }

    suspend fun getRandomCreatorsFromStorage(service: String, limit: Int): List<Creators> {
        return store.randomCreators(
            site = selectedSite.getSite(),
            service = service,
            limit = limit,
        )
    }
}