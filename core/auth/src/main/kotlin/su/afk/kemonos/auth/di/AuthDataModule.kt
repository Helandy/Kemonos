package su.afk.kemonos.auth.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.auth.data.local.AuthRepositoryImpl
import su.afk.kemonos.auth.domain.repository.AuthRepository
import su.afk.kemonos.auth.domain.repository.AuthSessionProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface AuthDataModule {

    @Binds
    @Singleton
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    fun bindAuthSessionProvider(impl: AuthRepositoryImpl): AuthSessionProvider
}
