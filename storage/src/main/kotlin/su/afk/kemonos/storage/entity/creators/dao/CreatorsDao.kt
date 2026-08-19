package su.afk.kemonos.storage.entity.creators.dao

import su.afk.kemonos.storage.entity.creators.CreatorsEntity

/**
 * Общий контракт DAO авторов.
 *
 * У каждого источника своя база, но набор запросов одинаковый: реализации различаются
 * только тем, к какой БД привязаны. Это позволяет выбирать DAO по [su.afk.kemonos.domain.SelectedSite]
 * через map, а не через when.
 */
interface CreatorsDao {

    suspend fun insertServices(entities: List<CreatorsEntity>)

    suspend fun clear()

    suspend fun replaceAllChunked(entities: List<CreatorsEntity>, chunkSize: Int = 5000)

    suspend fun getDistinctServices(): List<String>

    suspend fun searchPopularityAsc(service: String?, q: String, limit: Int, offset: Int): List<CreatorsEntity>
    suspend fun searchPopularityDesc(service: String?, q: String, limit: Int, offset: Int): List<CreatorsEntity>
    suspend fun searchIndexAsc(service: String?, q: String, limit: Int, offset: Int): List<CreatorsEntity>
    suspend fun searchIndexDesc(service: String?, q: String, limit: Int, offset: Int): List<CreatorsEntity>
    suspend fun searchUpdateAsc(service: String?, q: String, limit: Int, offset: Int): List<CreatorsEntity>
    suspend fun searchUpdateDesc(service: String?, q: String, limit: Int, offset: Int): List<CreatorsEntity>
    suspend fun searchNameAsc(service: String?, q: String, limit: Int, offset: Int): List<CreatorsEntity>
    suspend fun searchNameDesc(service: String?, q: String, limit: Int, offset: Int): List<CreatorsEntity>

    suspend fun randomCreators(service: String?, limit: Int): List<CreatorsEntity>

    suspend fun findByCompositeKeys(compositeKeys: Set<String>): List<CreatorsEntity>
}
