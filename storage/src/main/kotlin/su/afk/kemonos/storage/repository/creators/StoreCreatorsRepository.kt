package su.afk.kemonos.storage.repository.creators

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.CreatorsSort
import su.afk.kemonos.preferences.useCase.CacheKeys.CREATORS_COOMER
import su.afk.kemonos.preferences.useCase.CacheKeys.CREATORS_KEMONO
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_7_DAYS
import su.afk.kemonos.preferences.useCase.ICacheTimestampUseCase
import su.afk.kemonos.storage.entity.creators.CreatorsEntity
import su.afk.kemonos.storage.entity.creators.dao.CoomerCreatorsDao
import su.afk.kemonos.storage.entity.creators.dao.KemonoCreatorsDao
import javax.inject.Inject

interface IStoreCreatorsRepository {
    suspend fun updateCreators(site: SelectedSite, creators: List<CreatorsEntity>)
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
    ): List<CreatorsEntity>

    suspend fun randomCreators(
        site: SelectedSite,
        service: String,
        query: String,
        limit: Int,
    ): List<CreatorsEntity>
}

internal class StoreCreatorsRepository @Inject constructor(
    private val kemonoDao: KemonoCreatorsDao,
    private val coomerDao: CoomerCreatorsDao,
    private val cacheTimestamps: ICacheTimestampUseCase
) : IStoreCreatorsRepository {

    override suspend fun updateCreators(site: SelectedSite, creators: List<CreatorsEntity>) {
        when (site) {
            SelectedSite.K -> {
                kemonoDao.replaceAllChunked(creators)
            }
            SelectedSite.C -> {
                coomerDao.replaceAllChunked(creators)
            }
        }
        updateCacheTimestamp(site)
    }

    override suspend fun clear(site: SelectedSite) {
        when (site) {
            SelectedSite.K -> kemonoDao.clear()
            SelectedSite.C -> coomerDao.clear()
        }
        cacheTimestamps.clearCacheTimestamp(keyPref = key(site))
    }

    override suspend fun isCreatorsCacheFresh(site: SelectedSite): Boolean = isCacheFresh(site)

    override suspend fun getDistinctServices(site: SelectedSite): List<String> =
        when (site) {
            SelectedSite.K -> kemonoDao.getDistinctServices()
            SelectedSite.C -> coomerDao.getDistinctServices()
        }

    override suspend fun searchCreators(
        site: SelectedSite,
        service: String,
        query: String,
        sort: CreatorsSort,
        ascending: Boolean,
        limit: Int,
        offset: Int
    ): List<CreatorsEntity> {
        val s = service.ifBlank { "Services" }
        val q = query.trim()

        return when (site) {
            SelectedSite.K -> searchKemono(s, q, sort, ascending, limit, offset)
            SelectedSite.C -> searchCoomer(s, q, sort, ascending, limit, offset)
        }
    }

    private suspend fun searchKemono(
        service: String,
        q: String,
        sort: CreatorsSort,
        ascending: Boolean,
        limit: Int,
        offset: Int
    ): List<CreatorsEntity> = when (sort) {
        CreatorsSort.POPULARITY ->
            if (ascending) kemonoDao.searchPopularityAsc(service, q, limit, offset)
            else kemonoDao.searchPopularityDesc(service, q, limit, offset)

        CreatorsSort.INDEX ->
            if (ascending) kemonoDao.searchIndexAsc(service, q, limit, offset)
            else kemonoDao.searchIndexDesc(service, q, limit, offset)

        CreatorsSort.UPDATE ->
            if (ascending) kemonoDao.searchUpdateAsc(service, q, limit, offset)
            else kemonoDao.searchUpdateDesc(service, q, limit, offset)

        CreatorsSort.NAME ->
            if (ascending) kemonoDao.searchNameAsc(service, q, limit, offset)
            else kemonoDao.searchNameDesc(service, q, limit, offset)
    }

    private suspend fun searchCoomer(
        service: String,
        q: String,
        sort: CreatorsSort,
        ascending: Boolean,
        limit: Int,
        offset: Int
    ): List<CreatorsEntity> = when (sort) {
        CreatorsSort.POPULARITY ->
            if (ascending) coomerDao.searchPopularityAsc(service, q, limit, offset)
            else coomerDao.searchPopularityDesc(service, q, limit, offset)

        CreatorsSort.INDEX ->
            if (ascending) coomerDao.searchIndexAsc(service, q, limit, offset)
            else coomerDao.searchIndexDesc(service, q, limit, offset)

        CreatorsSort.UPDATE ->
            if (ascending) coomerDao.searchUpdateAsc(service, q, limit, offset)
            else coomerDao.searchUpdateDesc(service, q, limit, offset)

        CreatorsSort.NAME ->
            if (ascending) coomerDao.searchNameAsc(service, q, limit, offset)
            else coomerDao.searchNameDesc(service, q, limit, offset)
    }

    override suspend fun randomCreators(
        site: SelectedSite,
        service: String,
        query: String,
        limit: Int
    ): List<CreatorsEntity> = when (site) {
        SelectedSite.K -> kemonoDao.randomCreators(service, query.trim(), limit)
        SelectedSite.C -> coomerDao.randomCreators(service, query.trim(), limit)
    }

    private fun key(site: SelectedSite): String =
        if (site == SelectedSite.K) CREATORS_KEMONO else CREATORS_COOMER

    private fun getCacheTimestamp(site: SelectedSite): Long =
        cacheTimestamps.getCacheTimestamp(keyPref = key(site))

    private fun updateCacheTimestamp(site: SelectedSite) =
        cacheTimestamps.updateCacheTimestamp(keyPref = key(site))

    private fun isCacheFresh(site: SelectedSite): Boolean {
        val ts = getCacheTimestamp(site)
        if (ts == 0L) return false
        return System.currentTimeMillis() - ts < TTL_7_DAYS
    }
}