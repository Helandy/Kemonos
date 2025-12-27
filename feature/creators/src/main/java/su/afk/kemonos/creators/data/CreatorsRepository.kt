package su.afk.kemonos.creators.data

import su.afk.kemonos.common.data.creators.CreatorsDto.Companion.toDomain
import su.afk.kemonos.core.api.domain.net.helpers.call
import su.afk.kemonos.creators.data.api.CreatorsApi
import su.afk.kemonos.domain.domain.models.Creators
import su.afk.kemonos.domain.domain.models.CreatorsSort
import su.afk.kemonos.storage.api.StoreCreatorsUseCase
import javax.inject.Inject

interface ICreatorsRepository {
    suspend fun getCreators(): List<Creators>
}

internal class CreatorsRepository @Inject constructor(
    private val api: CreatorsApi,
    private val storeCreatorsUseCase: StoreCreatorsUseCase,
) : ICreatorsRepository {

    override suspend fun getCreators(): List<Creators> {
        val cached = storeCreatorsUseCase.searchCreators(
            service = "All",
            query = "",
            sort = CreatorsSort.POPULARITY,
            ascending = false,
            limit = 1000,
            offset = 0
        )

        val isFresh = storeCreatorsUseCase.isCreatorsCacheFresh()
        if (isFresh && cached.isNotEmpty()) return cached

        val fromNet = try {
            api.getCreators().call { list -> list.map { it.toDomain() } }
        } catch (t: Throwable) {
            if (cached.isNotEmpty()) return cached
            throw t
        }

        if (fromNet.isNotEmpty()) {
            storeCreatorsUseCase.updateCreators(fromNet)
            return fromNet
        }

        return cached
    }
}