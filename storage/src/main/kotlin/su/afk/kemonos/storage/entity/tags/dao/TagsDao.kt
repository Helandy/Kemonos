package su.afk.kemonos.storage.entity.tags.dao

import su.afk.kemonos.storage.entity.tags.TagsEntity

/**
 * Общий контракт DAO. У каждого источника своя база, но набор запросов одинаковый —
 * это позволяет выбирать DAO по источнику через map, а не через when.
 */
interface TagsDao {

    suspend fun getAll(): List<TagsEntity>

    suspend fun insertAll(items: List<TagsEntity>)

    suspend fun clear()

    suspend fun replaceAll(items: List<TagsEntity>)
}
