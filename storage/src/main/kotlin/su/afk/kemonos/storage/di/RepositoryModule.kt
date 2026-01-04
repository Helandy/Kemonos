package su.afk.kemonos.storage.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.storage.repository.comments.IStoreCommentsRepository
import su.afk.kemonos.storage.repository.comments.StoreCommentsRepository
import su.afk.kemonos.storage.repository.creatorProfileCache.IStoreCreatorProfileCacheRepository
import su.afk.kemonos.storage.repository.creatorProfileCache.StoreCreatorProfileCacheRepository
import su.afk.kemonos.storage.repository.creators.IStoreCreatorsRepository
import su.afk.kemonos.storage.repository.creators.StoreCreatorsRepository
import su.afk.kemonos.storage.repository.favorites.artist.IStoreFavoriteArtistsRepository
import su.afk.kemonos.storage.repository.favorites.artist.StoreFavoriteArtistsRepository
import su.afk.kemonos.storage.repository.favorites.post.IStoreFavoritePostsRepository
import su.afk.kemonos.storage.repository.favorites.post.StoreFavoritePostsRepository
import su.afk.kemonos.storage.repository.popular.IStoragePopularPostsCacheRepository
import su.afk.kemonos.storage.repository.popular.StoragePopularPostsCacheRepository
import su.afk.kemonos.storage.repository.post.IStoragePostStorageRepository
import su.afk.kemonos.storage.repository.post.StoragePostStorageRepository
import su.afk.kemonos.storage.repository.postsSearch.IPostsSearchCacheRepository
import su.afk.kemonos.storage.repository.postsSearch.PostsSearchCacheRepository
import su.afk.kemonos.storage.repository.profile.IStoreProfileRepository
import su.afk.kemonos.storage.repository.profile.StoreProfileRepository
import su.afk.kemonos.storage.repository.profilePosts.IStorageCreatorPostsCacheRepository
import su.afk.kemonos.storage.repository.profilePosts.StorageCreatorPostsCacheRepository
import su.afk.kemonos.storage.repository.tags.IStoreTagsRepository
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
    fun bindCreatorPostsCacheRepository(impl: StorageCreatorPostsCacheRepository): IStorageCreatorPostsCacheRepository

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
    fun bindPopularPostsCacheRepository(impl: StoragePopularPostsCacheRepository): IStoragePopularPostsCacheRepository

    @Singleton
    @Binds
    fun bindPostStorageRepository(impl: StoragePostStorageRepository): IStoragePostStorageRepository

    @Singleton
    @Binds
    fun bindPostsSearchCacheRepository(impl: PostsSearchCacheRepository): IPostsSearchCacheRepository

    @Singleton
    @Binds
    fun bindStoreFavoriteArtistsRepository(impl: StoreFavoriteArtistsRepository): IStoreFavoriteArtistsRepository

    @Singleton
    @Binds
    fun bindStoreCreatorProfileCacheRepository(impl: StoreCreatorProfileCacheRepository): IStoreCreatorProfileCacheRepository
}