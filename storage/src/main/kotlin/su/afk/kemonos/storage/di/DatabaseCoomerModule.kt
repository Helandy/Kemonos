package su.afk.kemonos.storage.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.storage.database.CoomerDatabase
import su.afk.kemonos.storage.database.migrations.coomer.*
import su.afk.kemonos.storage.entity.creators.dao.CoomerCreatorsDao
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
    fun provideCoomerDatabase(@ApplicationContext context: Context): CoomerDatabase =
        Room.databaseBuilder(context, CoomerDatabase::class.java, "coomer_db")
            .addMigrations(
                COOMER_MIGRATION_2_3,
                CoomerFrom3To4,
                CoomerFrom4To5,
                CoomerFrom5To6,
                CoomerFrom6To7
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
    fun provideCoomerPostsPopularCacheDao(db: CoomerDatabase): CoomerPostsPopularCacheDao =
        db.coomerPostsPopularCacheDao()
}
