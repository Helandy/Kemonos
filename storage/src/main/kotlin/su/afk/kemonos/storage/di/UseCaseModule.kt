package su.afk.kemonos.storage.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.storage.api.IStoreCommentsUseCase
import su.afk.kemonos.storage.api.StoreCreatorsUseCase
import su.afk.kemonos.storage.api.StoreProfileUseCase
import su.afk.kemonos.storage.api.clear.IClearCacheStorageUseCase
import su.afk.kemonos.storage.api.creatorProfileCache.IStoreCreatorProfileCacheUseCase
import su.afk.kemonos.storage.api.favorites.IStoreFavoriteArtistsUseCase
import su.afk.kemonos.storage.api.favorites.IStoreFavoritePostsUseCase
import su.afk.kemonos.storage.api.popular.IPopularPostsCacheUseCase
import su.afk.kemonos.storage.api.post.IPostUseCase
import su.afk.kemonos.storage.api.postsSearch.IPostsSearchCacheUseCase
import su.afk.kemonos.storage.api.profilePosts.ICreatorPostsCacheUseCase
import su.afk.kemonos.storage.api.tags.IStoreTagsUseCase
import su.afk.kemonos.storage.api.video.IVideoInfoUseCase
import su.afk.kemonos.storage.repository.video.IStoreVideoInfoRepository
import su.afk.kemonos.storage.repository.video.StoreVideoInfoRepository
import su.afk.kemonos.storage.useCases.StoreCommentsUseCase
import su.afk.kemonos.storage.useCases.StoreCreatorsUseCaseImpl
import su.afk.kemonos.storage.useCases.StoreProfileUseCaseImpl
import su.afk.kemonos.storage.useCases.clear.ClearCacheStorageUseCase
import su.afk.kemonos.storage.useCases.creatorProfileCache.StoreCreatorProfileCacheUseCase
import su.afk.kemonos.storage.useCases.favorites.StoreFavoriteArtistsUseCase
import su.afk.kemonos.storage.useCases.favorites.StoreFavoritePostsUseCase
import su.afk.kemonos.storage.useCases.popular.PopularPostsCacheUseCase
import su.afk.kemonos.storage.useCases.post.PostUseCase
import su.afk.kemonos.storage.useCases.postsSearch.PostsSearchCacheUseCase
import su.afk.kemonos.storage.useCases.profilePosts.CreatorPostsCacheUseCase
import su.afk.kemonos.storage.useCases.tags.StoreTagsUseCase
import su.afk.kemonos.storage.useCases.video.VideoInfoUseCase
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
    fun bindStoreCreatorsUseCase(impl: StoreCreatorsUseCaseImpl): StoreCreatorsUseCase

    @Singleton
    @Binds
    fun bindPopularPostsCacheUseCase(impl: PopularPostsCacheUseCase): IPopularPostsCacheUseCase

    @Singleton
    @Binds
    fun bindStoreCommentsUseCase(impl: StoreCommentsUseCase): IStoreCommentsUseCase

    @Singleton
    @Binds
    fun bindPostUseCase(impl: PostUseCase): IPostUseCase

    @Singleton
    @Binds
    fun bindStoreTagsUseCase(impl: StoreTagsUseCase): IStoreTagsUseCase

    @Singleton
    @Binds
    fun bindCreatorPostsCacheUseCase(impl: CreatorPostsCacheUseCase): ICreatorPostsCacheUseCase

    @Singleton
    @Binds
    fun bindVideoInfoUseCase(impl: VideoInfoUseCase): IVideoInfoUseCase

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

}