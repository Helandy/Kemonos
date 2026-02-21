package su.afk.kemonos.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import su.afk.kemonos.storage.entity.creators.CreatorsEntity
import su.afk.kemonos.storage.entity.creators.dao.CoomerCreatorsDao
import su.afk.kemonos.storage.entity.popular.PostsPopularCacheEntity
import su.afk.kemonos.storage.entity.popular.dao.CoomerPostsPopularCacheDao
import su.afk.kemonos.storage.entity.postsSearch.dao.CoomerPostsSearchCacheDao
import su.afk.kemonos.storage.entity.postsSearch.entity.PostsSearchCacheEntity
import su.afk.kemonos.storage.entity.postsSearch.history.PostsSearchHistoryEntity
import su.afk.kemonos.storage.entity.postsSearch.history.dao.CoomerPostsSearchHistoryDao
import su.afk.kemonos.storage.entity.tags.TagsEntity
import su.afk.kemonos.storage.entity.tags.dao.CoomerTagsDao

@Database(
    entities = [
        CreatorsEntity::class,

        TagsEntity::class,

        PostsSearchCacheEntity::class,
        PostsSearchHistoryEntity::class,

        PostsPopularCacheEntity::class,
    ],
    version = 7,
    exportSchema = false
)
internal abstract class CoomerDatabase : RoomDatabase() {
    abstract fun coomerCreatorsDao(): CoomerCreatorsDao
    abstract fun coomerTagsDao(): CoomerTagsDao

    abstract fun coomerPostsSearchCacheDao(): CoomerPostsSearchCacheDao
    abstract fun coomerPostsSearchHistoryDao(): CoomerPostsSearchHistoryDao

    abstract fun coomerPostsPopularCacheDao(): CoomerPostsPopularCacheDao
}
