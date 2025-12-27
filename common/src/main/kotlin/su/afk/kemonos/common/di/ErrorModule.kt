package su.afk.kemonos.common.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.common.error.AndroidStringProvider
import su.afk.kemonos.common.error.ErrorHandlerUseCaseImpl
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.StringProvider

@Module
@InstallIn(SingletonComponent::class)
interface ErrorModule {

    @Binds
    fun bindStringProvider(impl: AndroidStringProvider): StringProvider

    @Binds
    fun bindErrorHandlerUseCase(impl: ErrorHandlerUseCaseImpl): IErrorHandlerUseCase
}