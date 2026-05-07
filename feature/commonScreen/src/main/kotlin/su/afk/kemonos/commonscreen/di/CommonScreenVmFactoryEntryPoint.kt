package su.afk.kemonos.commonscreen.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.commonscreen.imageViewScreen.ImageViewViewModel

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface CommonScreenVmFactoryEntryPoint {
    fun imageViewVmFactory(): ImageViewViewModel.Factory
}
