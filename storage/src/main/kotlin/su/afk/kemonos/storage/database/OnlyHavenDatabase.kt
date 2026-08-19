package su.afk.kemonos.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import su.afk.kemonos.storage.entity.creators.CreatorsEntity
import su.afk.kemonos.storage.entity.creators.dao.OnlyHavenCreatorsDao
import su.afk.kemonos.storage.entity.dms.dao.OnlyHavenDmsCacheDao
import su.afk.kemonos.storage.entity.dms.entity.DmsCacheEntity
import su.afk.kemonos.storage.entity.popular.PostsPopularCacheEntity
import su.afk.kemonos.storage.entity.popular.dao.OnlyHavenPostsPopularCacheDao
import su.afk.kemonos.storage.entity.postsSearch.dao.OnlyHavenPostsSearchCacheDao
import su.afk.kemonos.storage.entity.postsSearch.entity.PostsSearchCacheEntity
import su.afk.kemonos.storage.entity.postsSearch.history.PostsSearchHistoryEntity
import su.afk.kemonos.storage.entity.postsSearch.history.dao.OnlyHavenPostsSearchHistoryDao
import su.afk.kemonos.storage.entity.tags.TagsEntity
import su.afk.kemonos.storage.entity.tags.dao.OnlyHavenTagsDao

/**
 * База OnlyHaven.
 *
 * Теги и популярное API не отдаёт — таблицы заводим, чтобы общие для всех источников
 * пути (очистка кэша, экраны настроек) работали единообразно; они просто остаются пустыми.
 */
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
internal abstract class OnlyHavenDatabase : RoomDatabase() {
    abstract fun onlyHavenCreatorsDao(): OnlyHavenCreatorsDao
    abstract fun onlyHavenTagsDao(): OnlyHavenTagsDao
    abstract fun onlyHavenPostsSearchCacheDao(): OnlyHavenPostsSearchCacheDao
    abstract fun onlyHavenPostsSearchHistoryDao(): OnlyHavenPostsSearchHistoryDao
    abstract fun onlyHavenDmsCacheDao(): OnlyHavenDmsCacheDao
    abstract fun onlyHavenPostsPopularCacheDao(): OnlyHavenPostsPopularCacheDao
}
