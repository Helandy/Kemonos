package su.afk.kemonos.posts.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.posts.api.ICheckApiUseCase
import su.afk.kemonos.posts.domain.usecase.CheckApiUseCase

@Module
@InstallIn(SingletonComponent::class)
internal interface UseCaseModule {

    @Binds
    fun bindCheckApiUseCase(impl: CheckApiUseCase): ICheckApiUseCase
}
