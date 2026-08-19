package su.afk.kemonos.storage.di

import su.afk.kemonos.storage.entity.tags.dao.TagsDao
import su.afk.kemonos.storage.entity.postsSearch.dao.PostsSearchCacheDao
import su.afk.kemonos.storage.entity.postsSearch.history.dao.PostsSearchHistoryDao
import su.afk.kemonos.storage.entity.dms.dao.DmsCacheDao
import su.afk.kemonos.storage.entity.popular.dao.PostsPopularCacheDao
import su.afk.kemonos.storage.entity.creators.dao.CreatorsDao
import dagger.multibindings.IntoMap
import su.afk.kemonos.domain.SelectedSite
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.preferences.useCase.CacheKeys
import su.afk.kemonos.storage.database.CoomerDatabase
import su.afk.kemonos.storage.database.migrations.DestructiveMigrationPrefSync
import su.afk.kemonos.storage.database.migrations.coomer.*
import su.afk.kemonos.storage.entity.creators.dao.CoomerCreatorsDao
import su.afk.kemonos.storage.entity.dms.dao.CoomerDmsCacheDao
import su.afk.kemonos.storage.entity.popular.dao.CoomerPostsPopularCacheDao
import su.afk.kemonos.storage.entity.postsSearch.dao.CoomerPostsSearchCacheDao
import su.afk.kemonos.storage.entity.postsSearch.history.dao.CoomerPostsSearchHistoryDao
import su.afk.kemonos.storage.entity.tags.dao.CoomerTagsDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseCoomerModule {

    @Provides
    @Singleton
    fun provideCoomerDatabase(
        @ApplicationContext context: Context,
        prefs: SharedPreferences,
    ): CoomerDatabase =
        Room.databaseBuilder(context, CoomerDatabase::class.java, "coomer_db")
            .addMigrations(
                *COOMER_DESTRUCTIVE_TO_10_MIGRATIONS,
                COOMER_MIGRATION_2_3,
                CoomerFrom3To4,
                CoomerFrom4To5,
                CoomerFrom5To6,
                CoomerFrom6To7,
                CoomerFrom7To8,
                CoomerFrom8To9,
                CoomerFrom9To10,
            )
            .addCallback(
                DestructiveMigrationPrefSync.createCleanupCallback(
                    scope = "coomer",
                    prefs = prefs,
                    keysToClearOnDestructiveRebuild = listOf(
                        CacheKeys.creators(SelectedSite.C),
                    ),
                    keysToClearWhenTableEmpty = mapOf(
                        "creators" to listOf(CacheKeys.creators(SelectedSite.C)),
                    ),
                )
            )
            .build()

    @Provides
    fun provideCoomerCreatorsDao(db: CoomerDatabase): CoomerCreatorsDao = db.coomerCreatorsDao()

    @Provides
    fun provideCoomerTagsDao(db: CoomerDatabase): CoomerTagsDao = db.coomerTagsDao()

    @Provides
    fun provideCoomerPostsSearchCacheDao(db: CoomerDatabase): CoomerPostsSearchCacheDao = db.coomerPostsSearchCacheDao()

    @Provides
    fun provideCoomerPostsSearchHistoryDao(db: CoomerDatabase): CoomerPostsSearchHistoryDao =
        db.coomerPostsSearchHistoryDao()

    @Provides
    fun provideCoomerDmsCacheDao(db: CoomerDatabase): CoomerDmsCacheDao = db.coomerDmsCacheDao()

    @Provides
    fun provideCoomerPostsPopularCacheDao(db: CoomerDatabase): CoomerPostsPopularCacheDao =
        db.coomerPostsPopularCacheDao()

    @Provides
    @IntoMap
    @SiteKey(SelectedSite.C)
    fun provideCoomerCreatorsDaoIntoMap(db: CoomerDatabase): CreatorsDao = db.coomerCreatorsDao()

    @Provides
    @IntoMap
    @SiteKey(SelectedSite.C)
    fun provideCoomerTagsDaoIntoMap(db: CoomerDatabase): TagsDao = db.coomerTagsDao()

    @Provides
    @IntoMap
    @SiteKey(SelectedSite.C)
    fun provideCoomerPostsSearchCacheDaoIntoMap(db: CoomerDatabase): PostsSearchCacheDao = db.coomerPostsSearchCacheDao()

    @Provides
    @IntoMap
    @SiteKey(SelectedSite.C)
    fun provideCoomerPostsSearchHistoryDaoIntoMap(db: CoomerDatabase): PostsSearchHistoryDao = db.coomerPostsSearchHistoryDao()

    @Provides
    @IntoMap
    @SiteKey(SelectedSite.C)
    fun provideCoomerDmsCacheDaoIntoMap(db: CoomerDatabase): DmsCacheDao = db.coomerDmsCacheDao()

    @Provides
    @IntoMap
    @SiteKey(SelectedSite.C)
    fun provideCoomerPostsPopularCacheDaoIntoMap(db: CoomerDatabase): PostsPopularCacheDao = db.coomerPostsPopularCacheDao()
}
