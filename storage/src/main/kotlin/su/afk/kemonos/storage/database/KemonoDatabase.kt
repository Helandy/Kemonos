package su.afk.kemonos.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import su.afk.kemonos.storage.database.converter.SelectedSiteConverters
import su.afk.kemonos.storage.entity.blacklist.BlacklistedAuthorEntity
import su.afk.kemonos.storage.entity.blacklist.dao.BlacklistedAuthorsDao
import su.afk.kemonos.storage.entity.comments.dao.CommentsDao
import su.afk.kemonos.storage.entity.comments.entity.CommentEntity
import su.afk.kemonos.storage.entity.comments.entity.CommentRevisionEntity
import su.afk.kemonos.storage.entity.creatorProfileCache.CreatorProfileCacheEntity
import su.afk.kemonos.storage.entity.creatorProfileCache.dao.CreatorProfileCacheDao
import su.afk.kemonos.storage.entity.creators.CreatorsEntity
import su.afk.kemonos.storage.entity.creators.dao.KemonoCreatorsDao
import su.afk.kemonos.storage.entity.download.DownloadTaskEntity
import su.afk.kemonos.storage.entity.download.dao.DownloadTaskDao
import su.afk.kemonos.storage.entity.favorites.artist.FavoriteArtistEntity
import su.afk.kemonos.storage.entity.favorites.artist.FavoriteArtistsDao
import su.afk.kemonos.storage.entity.favorites.post.FavoritePostEntity
import su.afk.kemonos.storage.entity.favorites.post.FavoritePostsDao
import su.afk.kemonos.storage.entity.favorites.updates.FreshFavoriteArtistUpdateEntity
import su.afk.kemonos.storage.entity.favorites.updates.FreshFavoriteArtistUpdatesDao
import su.afk.kemonos.storage.entity.popular.PostsPopularCacheEntity
import su.afk.kemonos.storage.entity.popular.dao.KemonoPostsPopularCacheDao
import su.afk.kemonos.storage.entity.post.PostContentCacheEntity
import su.afk.kemonos.storage.entity.post.dao.PostContentCacheDao
import su.afk.kemonos.storage.entity.postsSearch.dao.KemonoPostsSearchCacheDao
import su.afk.kemonos.storage.entity.postsSearch.entity.PostsSearchCacheEntity
import su.afk.kemonos.storage.entity.postsSearch.history.PostsSearchHistoryEntity
import su.afk.kemonos.storage.entity.postsSearch.history.dao.KemonoPostsSearchHistoryDao
import su.afk.kemonos.storage.entity.profile.ProfileEntity
import su.afk.kemonos.storage.entity.profile.dao.ProfileDao
import su.afk.kemonos.storage.entity.profilePosts.CreatorPostCacheEntity
import su.afk.kemonos.storage.entity.profilePosts.dao.CreatorPostsCacheDao
import su.afk.kemonos.storage.entity.tags.TagsEntity
import su.afk.kemonos.storage.entity.tags.dao.KemonoTagsDao
import su.afk.kemonos.storage.entity.video.VideoInfoEntity
import su.afk.kemonos.storage.entity.video.dao.VideoInfoDao

@Database(
    entities = [
        CreatorsEntity::class,

        FavoriteArtistEntity::class,
        FavoritePostEntity::class,
        FreshFavoriteArtistUpdateEntity::class,

        ProfileEntity::class,
        CreatorProfileCacheEntity::class,

        CommentEntity::class,
        CommentRevisionEntity::class,

        TagsEntity::class,

        VideoInfoEntity::class,

        CreatorPostCacheEntity::class,

        PostContentCacheEntity::class,

        PostsSearchCacheEntity::class,
        PostsSearchHistoryEntity::class,

        PostsPopularCacheEntity::class,

        DownloadTaskEntity::class,
        BlacklistedAuthorEntity::class,
    ],
    version = 12,
    exportSchema = false
)
@TypeConverters(SelectedSiteConverters::class)
internal abstract class KemonoDatabase : RoomDatabase() {
    abstract fun kemonoCreatorsDao(): KemonoCreatorsDao

    abstract fun favoriteArtistsDao(): FavoriteArtistsDao
    abstract fun favoritePostsDao(): FavoritePostsDao
    abstract fun freshFavoriteArtistUpdatesDao(): FreshFavoriteArtistUpdatesDao

    abstract fun kemonoProfileDao(): ProfileDao
    abstract fun creatorProfileCacheDao(): CreatorProfileCacheDao

    abstract fun commentsDao(): CommentsDao

    abstract fun kemonoTagsDao(): KemonoTagsDao

    abstract fun videoInfoDao(): VideoInfoDao

    abstract fun creatorPostsCacheDao(): CreatorPostsCacheDao

    abstract fun postContentCacheDao(): PostContentCacheDao

    abstract fun postsSearchCacheDao(): KemonoPostsSearchCacheDao
    abstract fun postsSearchHistoryDao(): KemonoPostsSearchHistoryDao

    abstract fun postsPopularCacheDao(): KemonoPostsPopularCacheDao

    abstract fun downloadTaskDao(): DownloadTaskDao

    abstract fun blacklistedAuthorsDao(): BlacklistedAuthorsDao
}
