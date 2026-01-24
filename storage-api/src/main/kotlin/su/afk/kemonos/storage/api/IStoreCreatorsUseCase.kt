package su.afk.kemonos.storage.api

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.Creators
import su.afk.kemonos.domain.models.CreatorsSort

interface IStoreCreatorsUseCase {
    suspend fun isCreatorsCacheFresh(): Boolean
    suspend fun updateCreators(creators: List<Creators>)
    suspend fun clear(site: SelectedSite? = null)

    suspend fun getDistinctServices(): List<String>

    suspend fun searchCreators(
        service: String,
        query: String,
        sort: CreatorsSort,
        ascending: Boolean,
        limit: Int,
        offset: Int,
    ): List<Creators>

    suspend fun randomCreators(
        service: String,
        query: String,
        limit: Int,
    ): List<Creators>
}