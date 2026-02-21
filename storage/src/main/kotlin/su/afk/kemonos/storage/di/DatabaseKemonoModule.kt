package su.afk.kemonos.storage.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.storage.database.KemonoDatabase
import su.afk.kemonos.storage.database.migrations.kemono.*
import su.afk.kemonos.storage.entity.blacklist.dao.BlacklistedAuthorsDao
import su.afk.kemonos.storage.entity.comments.dao.CommentsDao
import su.afk.kemonos.storage.entity.creatorProfileCache.dao.CreatorProfileCacheDao
import su.afk.kemonos.storage.entity.creators.dao.KemonoCreatorsDao
import su.afk.kemonos.storage.entity.download.dao.DownloadTaskDao
import su.afk.kemonos.storage.entity.favorites.artist.FavoriteArtistsDao
import su.afk.kemonos.storage.entity.favorites.post.FavoritePostsDao
import su.afk.kemonos.storage.entity.favorites.updates.FreshFavoriteArtistUpdatesDao
import su.afk.kemonos.storage.entity.popular.dao.KemonoPostsPopularCacheDao
import su.afk.kemonos.storage.entity.post.dao.PostContentCacheDao
import su.afk.kemonos.storage.entity.postsSearch.dao.KemonoPostsSearchCacheDao
import su.afk.kemonos.storage.entity.postsSearch.history.dao.KemonoPostsSearchHistoryDao
import su.afk.kemonos.storage.entity.profile.dao.ProfileDao
import su.afk.kemonos.storage.entity.profilePosts.dao.CreatorPostsCacheDao
import su.afk.kemonos.storage.entity.tags.dao.KemonoTagsDao
import su.afk.kemonos.storage.entity.video.dao.VideoInfoDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseKemonoModule {

    @Provides
    @Singleton
    fun provideKemonoDatabase(@ApplicationContext context: Context): KemonoDatabase =
        Room.databaseBuilder(context, KemonoDatabase::class.java, "kemono_db")
            .addMigrations(
                KEMONO_MIGRATION_2_3,
                KemonoFrom3To4,
                KemonoFrom4To5,
                KemonoFrom5To6,
                KemonoFrom6To7,
                KemonoFrom7To8,
                KemonoFrom8To9,
                KemonoFrom9To10,
                KemonoFrom10To11,
                KemonoFrom11To12,
            )
            .build()

    @Provides
    fun provideKemonoCreatorsDao(db: KemonoDatabase): KemonoCreatorsDao = db.kemonoCreatorsDao()

    @Provides
    fun provideProfileDao(db: KemonoDatabase): ProfileDao = db.kemonoProfileDao()

    @Provides
    fun provideCommentsDao(db: KemonoDatabase): CommentsDao = db.commentsDao()

    @Provides
    fun provideKemonoTagsDao(db: KemonoDatabase): KemonoTagsDao = db.kemonoTagsDao()

    @Provides
    fun provideVideoInfoDao(db: KemonoDatabase): VideoInfoDao = db.videoInfoDao()

    @Provides
    fun provideCreatorPostsCacheDao(db: KemonoDatabase): CreatorPostsCacheDao = db.creatorPostsCacheDao()

    @Provides
    fun provideFavoriteArtistsDao(db: KemonoDatabase): FavoriteArtistsDao = db.favoriteArtistsDao()

    @Provides
    fun provideFavoritePostsDao(db: KemonoDatabase): FavoritePostsDao = db.favoritePostsDao()

    @Provides
    fun provideFreshFavoriteArtistUpdatesDao(db: KemonoDatabase): FreshFavoriteArtistUpdatesDao =
        db.freshFavoriteArtistUpdatesDao()

    @Provides
    fun provideCreatorProfileCacheDao(db: KemonoDatabase): CreatorProfileCacheDao = db.creatorProfileCacheDao()

    @Provides
    fun providePostContentCacheDao(db: KemonoDatabase): PostContentCacheDao = db.postContentCacheDao()

    @Provides
    fun providePostsSearchCacheDao(db: KemonoDatabase): KemonoPostsSearchCacheDao = db.postsSearchCacheDao()

    @Provides
    fun providePostsSearchHistoryDao(db: KemonoDatabase): KemonoPostsSearchHistoryDao =
        db.postsSearchHistoryDao()

    @Provides
    fun providePostsPopularCacheDao(db: KemonoDatabase): KemonoPostsPopularCacheDao = db.postsPopularCacheDao()

    @Provides
    fun provideDownloadTaskDao(db: KemonoDatabase): DownloadTaskDao = db.downloadTaskDao()

    @Provides
    fun provideBlacklistedAuthorsDao(db: KemonoDatabase): BlacklistedAuthorsDao = db.blacklistedAuthorsDao()
}
