package su.afk.kemonos.error.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.error.error.AndroidStringProvider
import su.afk.kemonos.error.error.ErrorHandlerUseCaseImpl
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.StringProvider

@Module
@InstallIn(SingletonComponent::class)
interface ErrorModule {

    @Binds
    fun bindStringProvider(impl: AndroidStringProvider): StringProvider

    @Binds
    fun bindErrorHandlerUseCase(impl: ErrorHandlerUseCaseImpl): IErrorHandlerUseCase
}