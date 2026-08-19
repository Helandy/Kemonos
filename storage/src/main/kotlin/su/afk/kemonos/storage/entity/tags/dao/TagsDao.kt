package su.afk.kemonos.storage.entity.tags.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import su.afk.kemonos.storage.entity.tags.TagsEntity

/**
 * Общий контракт DAO тегов вместе с запросами.
 *
 * У каждого источника своя база, но таблица и запросы одинаковые, поэтому
 * конкретные DAO — пустые наследники: Room генерирует реализацию под свою БД.
 */
@Dao
interface TagsDao {

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
