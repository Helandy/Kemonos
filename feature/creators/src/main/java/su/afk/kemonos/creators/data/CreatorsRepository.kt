package su.afk.kemonos.creators.data

import kotlinx.coroutines.CancellationException
import su.afk.kemonos.creators.data.api.CreatorsApi
import su.afk.kemonos.creators.data.dto.CreatorsDto.Companion.toDomain
import su.afk.kemonos.creators.data.dto.RandomCreatorDto.Companion.toDomain
import su.afk.kemonos.creators.domain.random.RandomCreatorModel
import su.afk.kemonos.creators.domain.repository.ICreatorsRepository
import su.afk.kemonos.creators.data.dto.onlyhaven.OnlyHavenCreatorDto.Companion.toDomain
import su.afk.kemonos.creators.data.dto.onlyhaven.OnlyHavenCreatorDto.Companion.toOnlyHavenSort
import su.afk.kemonos.domain.capabilities
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.Creators
import su.afk.kemonos.domain.models.creator.CreatorsSort
import su.afk.kemonos.network.util.call
import su.afk.kemonos.storage.api.repository.creators.IStoreCreatorsRepository
import javax.inject.Inject

internal class CreatorsRepository @Inject constructor(
    private val apis: Map<SelectedSite, @JvmSuppressWildcards CreatorsApi>,
    private val storeCreatorsUseCase: IStoreCreatorsRepository,
) : ICreatorsRepository {

    override suspend fun getCreatorsPage(
        site: SelectedSite,
        service: String?,
        query: String,
        sort: CreatorsSort,
        limit: Int,
        offset: Int,
    ): List<Creators> = apis.getValue(site).run {
        getOnlyHavenCreators(
            offset = offset.takeIf { it > 0 },
            limit = limit,
            query = query.trim().ifEmpty { null },
            service = service,
            sort = sort.toOnlyHavenSort(),
        ).call { page -> page.creators.orEmpty().map { it.toDomain() } }
    }

    override suspend fun getCreators(site: SelectedSite): List<Creators> {
        /** Постраничный источник кэша авторов не имеет — список берётся пейджингом. */
        if (!site.capabilities.bulkCreatorList) return emptyList()

        val cached = storeCreatorsUseCase.searchCreators(
            site = site,
            service = null,
            query = "",
            sort = CreatorsSort.POPULARITY,
            ascending = false,
            limit = 1000,
            offset = 0
        )

        val isFresh = storeCreatorsUseCase.isCreatorsCacheFresh(site = site)
        if (isFresh && cached.isNotEmpty()) return cached

        val fromNet = try {
            apis.getValue(site).getCreators().call { list -> list.map { it.toDomain() } }
        } catch (t: Throwable) {
            if (t is CancellationException) throw t
            if (cached.isNotEmpty()) return cached
            throw t
        }

        if (fromNet.isNotEmpty()) {
            storeCreatorsUseCase.updateCreators(site = site, creators = fromNet)
            return fromNet
        }

        return cached
    }

    override suspend fun refreshCreatorsIfNeeded(site: SelectedSite): Boolean {
        if (!site.capabilities.bulkCreatorList) return false
        if (storeCreatorsUseCase.isCreatorsCacheFresh(site = site)) return false

        val fromNet = apis.getValue(site).getCreators().call { list -> list.map { it.toDomain() } }
        if (fromNet.isEmpty()) return false

        storeCreatorsUseCase.updateCreators(site = site, creators = fromNet)
        return true
    }

    override suspend fun randomCreator(site: SelectedSite): RandomCreatorModel = apis.getValue(site).run {
        randomCreator().call {
            it.toDomain()
        }
    }
}
