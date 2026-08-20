package su.afk.kemonos.creators.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.creators.domain.repository.ICreatorsRepository
import su.afk.kemonos.domain.capabilities
import su.afk.kemonos.domain.knownServices
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.Creators
import su.afk.kemonos.domain.models.creator.CreatorsSort
import su.afk.kemonos.storage.api.repository.creators.IStoreCreatorsRepository
import javax.inject.Inject

internal class GetCreatorsPagedUseCase @Inject constructor(
    private val store: IStoreCreatorsRepository,
    private val repository: ICreatorsRepository
) {
    /** Проверка свежий ли кэш */
    suspend fun checkFreshCache(site: SelectedSite): Boolean = repository.refreshCreatorsIfNeeded(site)

    fun paging(
        site: SelectedSite,
        service: String?,
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
                if (site.capabilities.bulkCreatorList) {
                    CreatorsPagingSource(
                        site = site,
                        store = store,
                        service = service,
                        query = query,
                        sort = sort,
                        ascending = ascending,
                    )
                } else {
                    /** Направление сортировки серверный эндпоинт не принимает. */
                    NetworkCreatorsPagingSource(
                        site = site,
                        repository = repository,
                        service = service,
                        query = query,
                        sort = sort,
                    )
                }
            }
        ).flow

    suspend fun getServices(site: SelectedSite): List<String> =
        site.knownServices.takeIf { it.isNotEmpty() }?.toList()
            ?: store.getDistinctServices(site = site)

    suspend fun getRandomCreatorsFromStorage(site: SelectedSite, service: String?, limit: Int): List<Creators> {
        return store.randomCreators(
            site = site,
            service = service,
            limit = limit,
        )
    }
}
