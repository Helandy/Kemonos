package su.afk.kemonos.creatorProfile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.creatorProfile.data.FavoritesCreatorRepository
import su.afk.kemonos.creatorProfile.data.IFavoritesCreatorRepository
import su.afk.kemonos.creatorProfile.data.IProfileRepository
import su.afk.kemonos.creatorProfile.data.ProfileRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Binds
    @Singleton
    fun bindFavoritesCreatorRepository(impl: FavoritesCreatorRepository): IFavoritesCreatorRepository

    @Binds
    @Singleton
    fun bindCreatorsRepository(impl: ProfileRepository): IProfileRepository
}