package su.afk.kemonos.profile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.profile.data.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Binds
    @Singleton
    fun bindAuthRepository(impl: AuthRepository): IAuthRepository

    @Binds
    @Singleton
    fun bindAccountRepository(impl: AccountRepository): IAccountRepository

    @Binds
    @Singleton
    fun provideFavoritesRepository(repository: FavoritesRepository): IFavoritesRepository
}