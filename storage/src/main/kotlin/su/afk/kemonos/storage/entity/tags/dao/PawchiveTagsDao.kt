package su.afk.kemonos.storage.entity.tags.dao

import androidx.room.*
import su.afk.kemonos.storage.entity.tags.TagsEntity

@Dao
interface PawchiveTagsDao : TagsDao {

    @Query("SELECT * FROM tags ORDER BY count DESC")
    override suspend fun getAll(): List<TagsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertAll(items: List<TagsEntity>)

    @Query("DELETE FROM tags")
    override suspend fun clear()

    @Transaction
    override suspend fun replaceAll(items: List<TagsEntity>) {
        clear()
        if (items.isNotEmpty()) insertAll(items)
    }
}
