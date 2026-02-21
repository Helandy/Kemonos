package su.afk.kemonos.storage.api.repository.creators

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.Creators
import su.afk.kemonos.domain.models.creator.CreatorsSort

interface IStoreCreatorsRepository {
    suspend fun updateCreators(site: SelectedSite, creators: List<Creators>)
    suspend fun clear(site: SelectedSite)
    suspend fun isCreatorsCacheFresh(site: SelectedSite): Boolean

    suspend fun getDistinctServices(site: SelectedSite): List<String>

    suspend fun searchCreators(
        site: SelectedSite,
        service: String,
        query: String,
        sort: CreatorsSort,
        ascending: Boolean,
        limit: Int,
        offset: Int,
    ): List<Creators>

    suspend fun randomCreators(
        site: SelectedSite,
        service: String,
        limit: Int,
    ): List<Creators>

    suspend fun getNamesByCompositeKeys(
        site: SelectedSite,
        compositeKeys: Set<String>,
    ): Map<String, String>
}
