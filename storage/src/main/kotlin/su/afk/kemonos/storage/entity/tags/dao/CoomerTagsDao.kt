package su.afk.kemonos.storage.entity.tags.dao

import androidx.room.*
import su.afk.kemonos.storage.entity.tags.TagsEntity

@Dao
interface CoomerTagsDao {

    @Query("SELECT * FROM tags ORDER BY count DESC")
    suspend fun getAll(): List<TagsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TagsEntity>)

    @Query("DELETE FROM tags")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(items: List<TagsEntity>) {
        clear()
        if (items.isNotEmpty()) insertAll(items)
    }
}