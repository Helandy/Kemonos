package su.afk.kemonos.storage.entity.creators.dao

import androidx.room.*
import su.afk.kemonos.storage.entity.creators.CreatorsEntity

@Dao
internal interface PawchiveCreatorsDao : CreatorsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertServices(entities: List<CreatorsEntity>)

    @Query("DELETE FROM creators")
    override suspend fun clear()

    @Transaction
    override suspend fun replaceAllChunked(entities: List<CreatorsEntity>, chunkSize: Int) {
        if (entities.isEmpty()) return
        clear()
        entities.chunked(chunkSize).forEach { insertServices(it) }
    }

    @Query("SELECT DISTINCT service FROM creators ORDER BY service ASC")
    override suspend fun getDistinctServices(): List<String>

    @Query(
        """
        SELECT * FROM creators
        WHERE (:service IS NULL OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY favorited ASC
        LIMIT :limit OFFSET :offset
        """
    )
    override suspend fun searchPopularityAsc(service: String?, q: String, limit: Int, offset: Int): List<CreatorsEntity>

    @Query(
        """
        SELECT * FROM creators
        WHERE (:service IS NULL OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY favorited DESC
        LIMIT :limit OFFSET :offset
        """
    )
    override suspend fun searchPopularityDesc(service: String?, q: String, limit: Int, offset: Int): List<CreatorsEntity>

    @Query(
        """
        SELECT * FROM creators
        WHERE (:service IS NULL OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY indexed ASC
        LIMIT :limit OFFSET :offset
        """
    )
    override suspend fun searchIndexAsc(service: String?, q: String, limit: Int, offset: Int): List<CreatorsEntity>

    @Query(
        """
        SELECT * FROM creators
        WHERE (:service IS NULL OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY indexed DESC
        LIMIT :limit OFFSET :offset
        """
    )
    override suspend fun searchIndexDesc(service: String?, q: String, limit: Int, offset: Int): List<CreatorsEntity>

    @Query(
        """
        SELECT * FROM creators
        WHERE (:service IS NULL OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY updated ASC
        LIMIT :limit OFFSET :offset
        """
    )
    override suspend fun searchUpdateAsc(service: String?, q: String, limit: Int, offset: Int): List<CreatorsEntity>

    @Query(
        """
        SELECT * FROM creators
        WHERE (:service IS NULL OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY updated DESC
        LIMIT :limit OFFSET :offset
        """
    )
    override suspend fun searchUpdateDesc(service: String?, q: String, limit: Int, offset: Int): List<CreatorsEntity>

    @Query(
        """
        SELECT * FROM creators
        WHERE (:service IS NULL OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY name COLLATE NOCASE ASC
        LIMIT :limit OFFSET :offset
        """
    )
    override suspend fun searchNameAsc(service: String?, q: String, limit: Int, offset: Int): List<CreatorsEntity>

    @Query(
        """
        SELECT * FROM creators
        WHERE (:service IS NULL OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY name COLLATE NOCASE DESC
        LIMIT :limit OFFSET :offset
        """
    )
    override suspend fun searchNameDesc(service: String?, q: String, limit: Int, offset: Int): List<CreatorsEntity>

    @Query(
        """
        SELECT * FROM creators
        WHERE (:service IS NULL OR service = :service)
        ORDER BY RANDOM()
        LIMIT :limit
        """
    )
    override suspend fun randomCreators(service: String?, limit: Int): List<CreatorsEntity>

    @Query(
        """
        SELECT * FROM creators
        WHERE (service || ':' || id) IN (:compositeKeys)
        """
    )
    override suspend fun findByCompositeKeys(compositeKeys: Set<String>): List<CreatorsEntity>
}
