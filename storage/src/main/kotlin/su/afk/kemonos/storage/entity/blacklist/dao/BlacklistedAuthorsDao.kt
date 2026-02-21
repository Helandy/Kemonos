package su.afk.kemonos.storage.entity.blacklist.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.storage.entity.blacklist.BlacklistedAuthorEntity

@Dao
internal interface BlacklistedAuthorsDao {
    @Query("SELECT * FROM blacklisted_authors ORDER BY creatorName ASC")
    fun observeAll(): Flow<List<BlacklistedAuthorEntity>>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM blacklisted_authors
            WHERE service = :service AND creatorId = :creatorId
        )
        """
    )
    fun observeContains(service: String, creatorId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: BlacklistedAuthorEntity)

    @Query("DELETE FROM blacklisted_authors WHERE service = :service AND creatorId = :creatorId")
    suspend fun remove(service: String, creatorId: String)

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM blacklisted_authors
            WHERE service = :service AND creatorId = :creatorId
        )
        """
    )
    suspend fun contains(service: String, creatorId: String): Boolean
}
