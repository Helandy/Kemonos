package su.afk.kemonos.creatorProfile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.creatorProfile.api.IGetProfileUseCase
import su.afk.kemonos.creatorProfile.domain.useCase.GetProfileUseCase

@Module
@InstallIn(SingletonComponent::class)
internal interface UseCaseModule {

    @Binds
    fun bindGetProfileUseCase(impl: GetProfileUseCase): IGetProfileUseCase
}
