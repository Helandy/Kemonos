package su.afk.kemonos.auth.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.auth.AuthLocalDataSource
import su.afk.kemonos.auth.IAuthLocalDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface UseCaseModule {

    @Binds
    @Singleton
    fun provideAuthLocalDataSource(impl: AuthLocalDataSource): IAuthLocalDataSource
}