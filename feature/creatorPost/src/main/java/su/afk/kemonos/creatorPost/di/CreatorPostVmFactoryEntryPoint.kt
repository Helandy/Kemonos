package su.afk.kemonos.creatorPost.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.creatorPost.presenter.CreatorPostViewModel

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface CreatorPostVmFactoryEntryPoint {
    fun creatorPostVmFactory(): CreatorPostViewModel.Factory
}