package su.afk.kemonos.storage.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.storage.api.IStoreCommentsUseCase
import su.afk.kemonos.storage.api.IStoreCreatorsUseCase
import su.afk.kemonos.storage.api.StoreProfileUseCase
import su.afk.kemonos.storage.api.clear.IClearCacheStorageUseCase
import su.afk.kemonos.storage.api.creatorProfileCache.IStoreCreatorProfileCacheUseCase
import su.afk.kemonos.storage.api.favorites.IStoreFavoriteArtistsUseCase
import su.afk.kemonos.storage.api.favorites.IStoreFavoritePostsUseCase
import su.afk.kemonos.storage.api.media.IMediaInfoUseCase
import su.afk.kemonos.storage.api.popular.IStoragePopularPostsCacheUseCase
import su.afk.kemonos.storage.api.post.IStoragePostUseCase
import su.afk.kemonos.storage.api.postsSearch.IPostsSearchCacheUseCase
import su.afk.kemonos.storage.api.profilePosts.IStorageCreatorPostsCacheUseCase
import su.afk.kemonos.storage.api.tags.IStoreTagsUseCase
import su.afk.kemonos.storage.api.video.IVideoFrameCache
import su.afk.kemonos.storage.repository.video.IStoreVideoInfoRepository
import su.afk.kemonos.storage.repository.video.StoreVideoInfoRepository
import su.afk.kemonos.storage.useCases.StoreCommentsUseCase
import su.afk.kemonos.storage.useCases.StoreCreatorsUseCase
import su.afk.kemonos.storage.useCases.StoreProfileUseCaseImpl
import su.afk.kemonos.storage.useCases.clear.ClearCacheStorageUseCase
import su.afk.kemonos.storage.useCases.creatorProfileCache.StoreCreatorProfileCacheUseCase
import su.afk.kemonos.storage.useCases.favorites.StoreFavoriteArtistsUseCase
import su.afk.kemonos.storage.useCases.favorites.StoreFavoritePostsUseCase
import su.afk.kemonos.storage.useCases.media.MediaInfoUseCase
import su.afk.kemonos.storage.useCases.popular.StoragePopularPostsCacheUseCase
import su.afk.kemonos.storage.useCases.post.StoragePostUseCase
import su.afk.kemonos.storage.useCases.postsSearch.PostsSearchCacheUseCase
import su.afk.kemonos.storage.useCases.profilePosts.StorageCreatorPostsCacheUseCase
import su.afk.kemonos.storage.useCases.tags.StoreTagsUseCase
import su.afk.kemonos.storage.useCases.video.VideoFrameCacheImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface UseCaseModule {

    @Singleton
    @Binds
    fun bindClearCacheStorage(impl: ClearCacheStorageUseCase): IClearCacheStorageUseCase

    @Singleton
    @Binds
    fun bindStoreProfileUseCase(impl: StoreProfileUseCaseImpl): StoreProfileUseCase

    @Singleton
    @Binds
    fun bindStoreCreatorsUseCase(impl: StoreCreatorsUseCase): IStoreCreatorsUseCase

    @Singleton
    @Binds
    fun bindPopularPostsCacheUseCase(impl: StoragePopularPostsCacheUseCase): IStoragePopularPostsCacheUseCase

    @Singleton
    @Binds
    fun bindStoreCommentsUseCase(impl: StoreCommentsUseCase): IStoreCommentsUseCase

    @Singleton
    @Binds
    fun bindPostUseCase(impl: StoragePostUseCase): IStoragePostUseCase

    @Singleton
    @Binds
    fun bindStoreTagsUseCase(impl: StoreTagsUseCase): IStoreTagsUseCase

    @Singleton
    @Binds
    fun bindCreatorPostsCacheUseCase(impl: StorageCreatorPostsCacheUseCase): IStorageCreatorPostsCacheUseCase

    @Singleton
    @Binds
    fun bindVideoInfoUseCase(impl: MediaInfoUseCase): IMediaInfoUseCase

    @Singleton
    @Binds
    fun bindPostsSearchCacheUseCase(impl: PostsSearchCacheUseCase): IPostsSearchCacheUseCase

    @Singleton
    @Binds
    fun bindStoreVideoInfoRepository(impl: StoreVideoInfoRepository): IStoreVideoInfoRepository

    @Singleton
    @Binds
    fun bindStoreFavoriteArtistsUseCase(impl: StoreFavoriteArtistsUseCase): IStoreFavoriteArtistsUseCase

    @Singleton
    @Binds
    fun bindStoreFavoritePostsUseCase(impl: StoreFavoritePostsUseCase): IStoreFavoritePostsUseCase

    @Singleton
    @Binds
    fun bindStoreCreatorProfileCacheUseCase(impl: StoreCreatorProfileCacheUseCase): IStoreCreatorProfileCacheUseCase

    @Singleton
    @Binds
    fun bindVideoFrameCache(impl: VideoFrameCacheImpl): IVideoFrameCache
}