package su.afk.kemonos.storage.entity.dms.dao

import su.afk.kemonos.storage.entity.dms.entity.DmsCacheEntity

/**
 * Общий контракт DAO. У каждого источника своя база, но набор запросов одинаковый —
 * это позволяет выбирать DAO по источнику через map, а не через when.
 */
interface DmsCacheDao {

    suspend fun getPage(queryKey: String, offset: Int): List<DmsCacheEntity>

    suspend fun getFreshPage(queryKey: String, offset: Int, minUpdatedAt: Long): List<DmsCacheEntity>

    suspend fun upsertAll(items: List<DmsCacheEntity>)

    suspend fun clearPage(queryKey: String, offset: Int)

    suspend fun replacePage(queryKey: String, offset: Int, items: List<DmsCacheEntity>)

    suspend fun deleteOlderThan(minUpdatedAt: Long)

    suspend fun clearAll()
}
