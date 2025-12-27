package su.afk.kemonos.creatorProfile.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.creatorProfile.presenter.CreatorProfileViewModel

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface CreatorProfileVmFactoryEntryPoint {
    fun creatorProfileVmFactory(): CreatorProfileViewModel.Factory
}