package su.afk.kemonos.storage.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.storage.api.clear.IClearCacheStorageUseCase
import su.afk.kemonos.storage.api.repository.media.IStoreMediaInfoRepository
import su.afk.kemonos.storage.api.videoPreview.IVideoFrameCache
import su.afk.kemonos.storage.repository.video.StoreMediaInfoRepository
import su.afk.kemonos.storage.useCases.clear.ClearCacheStorageUseCase
import su.afk.kemonos.storage.useCases.videoPreview.VideoFrameCacheImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface UseCaseModule {

    @Singleton
    @Binds
    fun bindClearCacheStorage(impl: ClearCacheStorageUseCase): IClearCacheStorageUseCase

    @Singleton
    @Binds
    fun bindStoreVideoInfoRepository(impl: StoreMediaInfoRepository): IStoreMediaInfoRepository

    @Singleton
    @Binds
    fun bindVideoFrameCache(impl: VideoFrameCacheImpl): IVideoFrameCache
}