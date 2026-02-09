package su.afk.kemonos.app.update.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.app.update.api.useCase.ICheckAppUpdateUseCase
import su.afk.kemonos.app.update.domain.useCase.CheckAppUpdateUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface UseCaseModule {

    @Binds
    @Singleton
    fun bindIsCheckAppUpdateUseCase(impl: CheckAppUpdateUseCase): ICheckAppUpdateUseCase
}