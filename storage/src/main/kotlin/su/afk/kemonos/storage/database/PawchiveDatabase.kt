package su.afk.kemonos.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import su.afk.kemonos.storage.entity.creators.CreatorsEntity
import su.afk.kemonos.storage.entity.creators.dao.PawchiveCreatorsDao
import su.afk.kemonos.storage.entity.dms.dao.PawchiveDmsCacheDao
import su.afk.kemonos.storage.entity.dms.entity.DmsCacheEntity
import su.afk.kemonos.storage.entity.popular.PostsPopularCacheEntity
import su.afk.kemonos.storage.entity.popular.dao.PawchivePostsPopularCacheDao
import su.afk.kemonos.storage.entity.postsSearch.dao.PawchivePostsSearchCacheDao
import su.afk.kemonos.storage.entity.postsSearch.entity.PostsSearchCacheEntity
import su.afk.kemonos.storage.entity.postsSearch.history.PostsSearchHistoryEntity
import su.afk.kemonos.storage.entity.postsSearch.history.dao.PawchivePostsSearchHistoryDao
import su.afk.kemonos.storage.entity.tags.TagsEntity
import su.afk.kemonos.storage.entity.tags.dao.PawchiveTagsDao

@Database(
    entities = [
        CreatorsEntity::class,
        TagsEntity::class,
        PostsSearchCacheEntity::class,
        PostsSearchHistoryEntity::class,
        DmsCacheEntity::class,
        PostsPopularCacheEntity::class,
    ],
    version = 1,
    exportSchema = false
)
internal abstract class PawchiveDatabase : RoomDatabase() {
    abstract fun pawchiveCreatorsDao(): PawchiveCreatorsDao
    abstract fun pawchiveTagsDao(): PawchiveTagsDao
    abstract fun pawchivePostsSearchCacheDao(): PawchivePostsSearchCacheDao
    abstract fun pawchivePostsSearchHistoryDao(): PawchivePostsSearchHistoryDao
    abstract fun pawchiveDmsCacheDao(): PawchiveDmsCacheDao
    abstract fun pawchivePostsPopularCacheDao(): PawchivePostsPopularCacheDao
}
