package su.afk.kemonos.storage.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.useCase.CacheKeys
import su.afk.kemonos.storage.database.OnlyHavenDatabase
import su.afk.kemonos.storage.database.migrations.DestructiveMigrationPrefSync
import su.afk.kemonos.storage.entity.creators.dao.CreatorsDao
import su.afk.kemonos.storage.entity.dms.dao.DmsCacheDao
import su.afk.kemonos.storage.entity.popular.dao.PostsPopularCacheDao
import su.afk.kemonos.storage.entity.postsSearch.dao.PostsSearchCacheDao
import su.afk.kemonos.storage.entity.postsSearch.history.dao.PostsSearchHistoryDao
import su.afk.kemonos.storage.entity.tags.dao.TagsDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseOnlyHavenModule {

    @Provides
    @Singleton
    fun provideOnlyHavenDatabase(
        @ApplicationContext context: Context,
        prefs: SharedPreferences,
    ): OnlyHavenDatabase =
        Room.databaseBuilder(context, OnlyHavenDatabase::class.java, "onlyhaven_db")
            .addCallback(
                DestructiveMigrationPrefSync.createCleanupCallback(
                    scope = "onlyhaven",
                    prefs = prefs,
                    keysToClearOnDestructiveRebuild = listOf(
                        CacheKeys.creators(SelectedSite.O),
                    ),
                    keysToClearWhenTableEmpty = mapOf(
                        "creators" to listOf(CacheKeys.creators(SelectedSite.O)),
                    ),
                )
            )
            .build()

    @Provides
    @IntoMap
    @SiteKey(SelectedSite.O)
    fun provideOnlyHavenCreatorsDaoIntoMap(db: OnlyHavenDatabase): CreatorsDao =
        db.onlyHavenCreatorsDao()

    @Provides
    @IntoMap
    @SiteKey(SelectedSite.O)
    fun provideOnlyHavenTagsDaoIntoMap(db: OnlyHavenDatabase): TagsDao =
        db.onlyHavenTagsDao()

    @Provides
    @IntoMap
    @SiteKey(SelectedSite.O)
    fun provideOnlyHavenPostsSearchCacheDaoIntoMap(db: OnlyHavenDatabase): PostsSearchCacheDao =
        db.onlyHavenPostsSearchCacheDao()

    @Provides
    @IntoMap
    @SiteKey(SelectedSite.O)
    fun provideOnlyHavenPostsSearchHistoryDaoIntoMap(db: OnlyHavenDatabase): PostsSearchHistoryDao =
        db.onlyHavenPostsSearchHistoryDao()

    @Provides
    @IntoMap
    @SiteKey(SelectedSite.O)
    fun provideOnlyHavenDmsCacheDaoIntoMap(db: OnlyHavenDatabase): DmsCacheDao =
        db.onlyHavenDmsCacheDao()

    @Provides
    @IntoMap
    @SiteKey(SelectedSite.O)
    fun provideOnlyHavenPostsPopularCacheDaoIntoMap(db: OnlyHavenDatabase): PostsPopularCacheDao =
        db.onlyHavenPostsPopularCacheDao()
}
