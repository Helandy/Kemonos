package su.afk.kemonos.storage.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.preferences.useCase.CacheKeys
import su.afk.kemonos.storage.database.PawchiveDatabase
import su.afk.kemonos.storage.database.migrations.DestructiveMigrationPrefSync
import su.afk.kemonos.storage.entity.creators.dao.PawchiveCreatorsDao
import su.afk.kemonos.storage.entity.dms.dao.PawchiveDmsCacheDao
import su.afk.kemonos.storage.entity.popular.dao.PawchivePostsPopularCacheDao
import su.afk.kemonos.storage.entity.postsSearch.dao.PawchivePostsSearchCacheDao
import su.afk.kemonos.storage.entity.postsSearch.history.dao.PawchivePostsSearchHistoryDao
import su.afk.kemonos.storage.entity.tags.dao.PawchiveTagsDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabasePawchiveModule {

    @Provides
    @Singleton
    fun providePawchiveDatabase(
        @ApplicationContext context: Context,
        prefs: SharedPreferences,
    ): PawchiveDatabase =
        Room.databaseBuilder(context, PawchiveDatabase::class.java, "pawchive_db")
            .addCallback(
                DestructiveMigrationPrefSync.createCleanupCallback(
                    scope = "pawchive",
                    prefs = prefs,
                    keysToClearOnDestructiveRebuild = listOf(
                        CacheKeys.CREATORS_PAWCHIVE,
                    ),
                    keysToClearWhenTableEmpty = mapOf(
                        "creators" to listOf(CacheKeys.CREATORS_PAWCHIVE),
                    ),
                )
            )
            .build()

    @Provides
    fun providePawchiveCreatorsDao(db: PawchiveDatabase): PawchiveCreatorsDao = db.pawchiveCreatorsDao()

    @Provides
    fun providePawchiveTagsDao(db: PawchiveDatabase): PawchiveTagsDao = db.pawchiveTagsDao()

    @Provides
    fun providePawchivePostsSearchCacheDao(db: PawchiveDatabase): PawchivePostsSearchCacheDao =
        db.pawchivePostsSearchCacheDao()

    @Provides
    fun providePawchivePostsSearchHistoryDao(db: PawchiveDatabase): PawchivePostsSearchHistoryDao =
        db.pawchivePostsSearchHistoryDao()

    @Provides
    fun providePawchiveDmsCacheDao(db: PawchiveDatabase): PawchiveDmsCacheDao = db.pawchiveDmsCacheDao()

    @Provides
    fun providePawchivePostsPopularCacheDao(db: PawchiveDatabase): PawchivePostsPopularCacheDao =
        db.pawchivePostsPopularCacheDao()
}
