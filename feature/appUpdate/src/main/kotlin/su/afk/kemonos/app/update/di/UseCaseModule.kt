package su.afk.kemonos.app.update.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.app.update.api.useCase.ICheckAppUpdateUseCase
import su.afk.kemonos.app.update.domain.CheckAppUpdateUseCase

@Module
@InstallIn(SingletonComponent::class)
internal interface UseCaseModule {

    @Binds
    fun bindIsCheckAppUpdateUseCase(impl: CheckAppUpdateUseCase): ICheckAppUpdateUseCase
}