package su.afk.kemonos.profile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.profile.data.file.JsonFileStorage
import su.afk.kemonos.profile.data.parser.BlacklistJsonParser
import su.afk.kemonos.profile.data.parser.FavoritesJsonParser
import su.afk.kemonos.profile.data.repository.AccountRepository
import su.afk.kemonos.profile.data.repository.AuthRepository
import su.afk.kemonos.profile.data.repository.FavoritesRepository
import su.afk.kemonos.profile.data.repository.ImportExportRepository
import su.afk.kemonos.profile.domain.blacklist.IBlacklistJsonParser
import su.afk.kemonos.profile.domain.favorites.IFavoritesJsonParser
import su.afk.kemonos.profile.domain.file.IJsonFileStorage
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

    @Binds
    @Singleton
    fun bindJsonFileStorage(impl: JsonFileStorage): IJsonFileStorage

    @Binds
    fun bindFavoritesJsonParser(impl: FavoritesJsonParser): IFavoritesJsonParser

    @Binds
    fun bindBlacklistJsonParser(impl: BlacklistJsonParser): IBlacklistJsonParser
}
