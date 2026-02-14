package su.afk.kemonos.storage.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.storage.api.repository.comments.IStoreCommentsRepository
import su.afk.kemonos.storage.api.repository.creatorProfile.IStoreCreatorProfileRepository
import su.afk.kemonos.storage.api.repository.creators.IStoreCreatorsRepository
import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository
import su.afk.kemonos.storage.api.repository.favorites.artist.IStoreFavoriteArtistsRepository
import su.afk.kemonos.storage.api.repository.favorites.post.IStoreFavoritePostsRepository
import su.afk.kemonos.storage.api.repository.favorites.updates.IFreshFavoriteArtistsUpdatesRepository
import su.afk.kemonos.storage.api.repository.popular.IStoragePopularPostsRepository
import su.afk.kemonos.storage.api.repository.post.IStoragePostStorageRepository
import su.afk.kemonos.storage.api.repository.postsSearch.IStoragePostsSearchRepository
import su.afk.kemonos.storage.api.repository.profile.IStoreProfileRepository
import su.afk.kemonos.storage.api.repository.profilePosts.IStorageCreatorPostsRepository
import su.afk.kemonos.storage.api.repository.tags.IStoreTagsRepository
import su.afk.kemonos.storage.repository.comments.StoreCommentsRepository
import su.afk.kemonos.storage.repository.creatorProfileCache.StoreCreatorProfileRepository
import su.afk.kemonos.storage.repository.creators.StoreCreatorsRepository
import su.afk.kemonos.storage.repository.download.TrackedDownloadsRepository
import su.afk.kemonos.storage.repository.favorites.artist.StoreFavoriteArtistsRepository
import su.afk.kemonos.storage.repository.favorites.post.StoreFavoritePostsRepository
import su.afk.kemonos.storage.repository.favorites.updates.FreshFavoriteArtistsUpdatesRepository
import su.afk.kemonos.storage.repository.popular.StoragePopularPostsRepository
import su.afk.kemonos.storage.repository.post.StoragePostStorageRepository
import su.afk.kemonos.storage.repository.postsSearch.StoragePostsSearchRepository
import su.afk.kemonos.storage.repository.profile.StoreProfileRepository
import su.afk.kemonos.storage.repository.profilePosts.StorageCreatorPostsRepository
import su.afk.kemonos.storage.repository.tags.StoreTagsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Singleton
    @Binds
    fun bindStoreCreatorsRepository(impl: StoreCreatorsRepository): IStoreCreatorsRepository

    @Singleton
    @Binds
    fun bindStoreProfileRepository(impl: StoreProfileRepository): IStoreProfileRepository

    @Singleton
    @Binds
    fun bindCreatorPostsCacheRepository(impl: StorageCreatorPostsRepository): IStorageCreatorPostsRepository

    @Singleton
    @Binds
    fun bindStoreTagsRepository(impl: StoreTagsRepository): IStoreTagsRepository

    @Singleton
    @Binds
    fun bindStoreCommentsRepository(impl: StoreCommentsRepository): IStoreCommentsRepository

    @Singleton
    @Binds
    fun bindStoreFavoritePostsRepository(impl: StoreFavoritePostsRepository): IStoreFavoritePostsRepository

    @Singleton
    @Binds
    fun bindPopularPostsCacheRepository(impl: StoragePopularPostsRepository): IStoragePopularPostsRepository

    @Singleton
    @Binds
    fun bindPostStorageRepository(impl: StoragePostStorageRepository): IStoragePostStorageRepository

    @Singleton
    @Binds
    fun bindPostsSearchCacheRepository(impl: StoragePostsSearchRepository): IStoragePostsSearchRepository

    @Singleton
    @Binds
    fun bindStoreFavoriteArtistsRepository(impl: StoreFavoriteArtistsRepository): IStoreFavoriteArtistsRepository

    @Singleton
    @Binds
    fun bindFreshFavoriteArtistsUpdatesRepository(
        impl: FreshFavoriteArtistsUpdatesRepository
    ): IFreshFavoriteArtistsUpdatesRepository

    @Singleton
    @Binds
    fun bindStoreCreatorProfileCacheRepository(impl: StoreCreatorProfileRepository): IStoreCreatorProfileRepository

    @Singleton
    @Binds
    fun bindTrackedDownloadsRepository(impl: TrackedDownloadsRepository): ITrackedDownloadsRepository
}
