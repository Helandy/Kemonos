package su.afk.kemonos.storage.api

import su.afk.kemonos.domain.models.Creators
import su.afk.kemonos.domain.models.CreatorsSort

interface StoreCreatorsUseCase {
    suspend fun isCreatorsCacheFresh(): Boolean
    suspend fun updateCreators(creators: List<Creators>)
    suspend fun clear()

    suspend fun getDistinctServices(): List<String>

    /**
     * Просто получить результаты (для маленьких списков).
     * Для больших списков paging делаем в feature-модуле, где есть AndroidX Paging.
     */
    suspend fun searchCreators(
        service: String,
        query: String,
        sort: CreatorsSort,
        ascending: Boolean,
        limit: Int,
        offset: Int,
    ): List<Creators>
}