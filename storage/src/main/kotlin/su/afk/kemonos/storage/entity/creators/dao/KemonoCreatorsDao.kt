package su.afk.kemonos.storage.entity.creators.dao

import androidx.room.*
import su.afk.kemonos.storage.entity.creators.CreatorsEntity

@Dao
internal interface KemonoCreatorsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<CreatorsEntity>)

    @Query("DELETE FROM creators")
    suspend fun clear()

    @Transaction
    suspend fun replaceAllChunked(entities: List<CreatorsEntity>, chunkSize: Int = 2000) {
        clear()
        entities.chunked(chunkSize).forEach { insertAll(it) }
    }

    @Query("SELECT DISTINCT service FROM creators ORDER BY service ASC")
    suspend fun getDistinctServices(): List<String>

    // --- SEARCH: POPULARITY ---
    @Query(
        """
        SELECT * FROM creators
        WHERE (:service = 'All' OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY favorited ASC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun searchPopularityAsc(service: String, q: String, limit: Int, offset: Int): List<CreatorsEntity>

    @Query(
        """
        SELECT * FROM creators
        WHERE (:service = 'All' OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY favorited DESC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun searchPopularityDesc(service: String, q: String, limit: Int, offset: Int): List<CreatorsEntity>

    // --- SEARCH: INDEX ---
    @Query(
        """
        SELECT * FROM creators
        WHERE (:service = 'All' OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY indexed ASC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun searchIndexAsc(service: String, q: String, limit: Int, offset: Int): List<CreatorsEntity>

    @Query(
        """
        SELECT * FROM creators
        WHERE (:service = 'All' OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY indexed DESC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun searchIndexDesc(service: String, q: String, limit: Int, offset: Int): List<CreatorsEntity>

    // --- SEARCH: UPDATE ---
    @Query(
        """
        SELECT * FROM creators
        WHERE (:service = 'All' OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY updated ASC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun searchUpdateAsc(service: String, q: String, limit: Int, offset: Int): List<CreatorsEntity>

    @Query(
        """
        SELECT * FROM creators
        WHERE (:service = 'All' OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY updated DESC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun searchUpdateDesc(service: String, q: String, limit: Int, offset: Int): List<CreatorsEntity>

    // --- SEARCH: NAME ---
    @Query(
        """
        SELECT * FROM creators
        WHERE (:service = 'All' OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY name COLLATE NOCASE ASC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun searchNameAsc(service: String, q: String, limit: Int, offset: Int): List<CreatorsEntity>

    @Query(
        """
        SELECT * FROM creators
        WHERE (:service = 'All' OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY name COLLATE NOCASE DESC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun searchNameDesc(service: String, q: String, limit: Int, offset: Int): List<CreatorsEntity>
}