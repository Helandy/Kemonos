package su.afk.kemonos.commonscreen.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.commonscreen.errorScreen.ErrorViewModel

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface ErrorNavigatorEntryPoint {
    fun creatorErrorViewModelFactory(): ErrorViewModel.Factory
}