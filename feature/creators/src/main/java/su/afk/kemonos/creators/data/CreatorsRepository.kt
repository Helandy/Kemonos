package su.afk.kemonos.creators.data

import su.afk.kemonos.creators.data.api.CreatorsApi
import su.afk.kemonos.creators.data.dto.CreatorsDto.Companion.toDomain
import su.afk.kemonos.creators.data.dto.RandomCreatorDto.Companion.toDomain
import su.afk.kemonos.creators.domain.model.RandomCreator
import su.afk.kemonos.domain.models.creator.Creators
import su.afk.kemonos.domain.models.creator.CreatorsSort
import su.afk.kemonos.network.util.call
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.storage.api.repository.creators.IStoreCreatorsRepository
import javax.inject.Inject

interface ICreatorsRepository {
    suspend fun getCreators(): List<Creators>
    suspend fun refreshCreatorsIfNeeded(): Boolean
    suspend fun randomCreator(): RandomCreator
}

internal class CreatorsRepository @Inject constructor(
    private val api: CreatorsApi,
    private val storeCreatorsUseCase: IStoreCreatorsRepository,
    private val selectedSite: ISelectedSiteUseCase,
) : ICreatorsRepository {

    override suspend fun getCreators(): List<Creators> {
        val cached = storeCreatorsUseCase.searchCreators(
            site = selectedSite.getSite(),
            service = "Services",
            query = "",
            sort = CreatorsSort.POPULARITY,
            ascending = false,
            limit = 1000,
            offset = 0
        )

        val isFresh = storeCreatorsUseCase.isCreatorsCacheFresh(site = selectedSite.getSite())
        if (isFresh && cached.isNotEmpty()) return cached

        val fromNet = try {
            api.getCreators().call { list -> list.map { it.toDomain() } }
        } catch (t: Throwable) {
            if (cached.isNotEmpty()) return cached
            throw t
        }

        if (fromNet.isNotEmpty()) {
            storeCreatorsUseCase.updateCreators(site = selectedSite.getSite(), creators = fromNet)
            return fromNet
        }

        return cached
    }

    override suspend fun refreshCreatorsIfNeeded(): Boolean {
        if (storeCreatorsUseCase.isCreatorsCacheFresh(site = selectedSite.getSite())) return false

        val fromNet = api.getCreators().call { list -> list.map { it.toDomain() } }
        if (fromNet.isEmpty()) return false

        storeCreatorsUseCase.updateCreators(site = selectedSite.getSite(), creators = fromNet)
        return true
    }

    override suspend fun randomCreator(): RandomCreator = api.randomCreator().call {
        it.toDomain()
    }
}