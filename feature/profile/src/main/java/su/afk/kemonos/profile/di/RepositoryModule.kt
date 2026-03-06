package su.afk.kemonos.profile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.profile.data.AccountRepository
import su.afk.kemonos.profile.data.AuthRepository
import su.afk.kemonos.profile.data.FavoritesRepository
import su.afk.kemonos.profile.data.ImportExportRepository
import su.afk.kemonos.profile.domain.IAuthRepository
import su.afk.kemonos.profile.domain.account.IAccountRepository
import su.afk.kemonos.profile.domain.favorites.IFavoritesRepository
import su.afk.kemonos.profile.domain.favorites.IImportExportRepository
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

    @Binds
    @Singleton
    fun provideImportExportRepository(repository: ImportExportRepository): IImportExportRepository
}
