package su.afk.kemonos.storage.useCases

import su.afk.kemonos.domain.domain.models.Creators
import su.afk.kemonos.domain.domain.models.CreatorsSort
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.storage.api.StoreCreatorsUseCase
import su.afk.kemonos.storage.entity.creators.CreatorsEntity
import su.afk.kemonos.storage.entity.creators.CreatorsEntity.Companion.toDomain
import su.afk.kemonos.storage.entity.creators.CreatorsEntity.Companion.toEntity
import su.afk.kemonos.storage.repository.creators.IStoreCreatorsRepository
import su.afk.kemonos.utils.withIo
import javax.inject.Inject

internal class StoreCreatorsUseCaseImpl @Inject constructor(
    private val creatorsRepository: IStoreCreatorsRepository,
    private val selectedSite: ISelectedSiteUseCase,
) : StoreCreatorsUseCase {

    override suspend fun isCreatorsCacheFresh(): Boolean = withIo {
        creatorsRepository.isCreatorsCacheFresh(site = selectedSite.getSite())
    }

    override suspend fun updateCreators(creators: List<Creators>) = withIo {
        val entities: List<CreatorsEntity> = creators.map { it.toEntity() }
        creatorsRepository.updateCreators(site = selectedSite.getSite(), creators = entities)
    }

    override suspend fun clear() = withIo {
        creatorsRepository.clear(site = selectedSite.getSite())
    }

    override suspend fun getDistinctServices(): List<String> = withIo {
        creatorsRepository.getDistinctServices(site = selectedSite.getSite())
    }

    override suspend fun searchCreators(
        service: String,
        query: String,
        sort: CreatorsSort,
        ascending: Boolean,
        limit: Int,
        offset: Int
    ): List<Creators> = withIo {
        creatorsRepository.searchCreators(
            site = selectedSite.getSite(),
            service = service,
            query = query,
            sort = sort,
            ascending = ascending,
            limit = limit,
            offset = offset
        ).map { it.toDomain() }
    }
}