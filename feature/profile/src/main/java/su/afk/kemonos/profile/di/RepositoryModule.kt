package su.afk.kemonos.profile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.profile.data.repository.AccountRepository
import su.afk.kemonos.profile.data.repository.AuthRepository
import su.afk.kemonos.profile.data.repository.FavoritesRepository
import su.afk.kemonos.profile.data.repository.ImportExportRepository
import su.afk.kemonos.profile.domain.repository.IAccountRepository
import su.afk.kemonos.profile.domain.repository.IAuthRepository
import su.afk.kemonos.profile.domain.repository.IFavoritesRepository
import su.afk.kemonos.profile.domain.repository.IImportExportRepository
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
