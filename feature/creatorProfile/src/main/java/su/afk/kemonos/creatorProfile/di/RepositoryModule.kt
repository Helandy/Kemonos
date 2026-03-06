package su.afk.kemonos.creatorProfile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.creatorProfile.data.CreatorsRepository
import su.afk.kemonos.creatorProfile.data.DiscordRepository
import su.afk.kemonos.creatorProfile.data.FavoritesCreatorRepository
import su.afk.kemonos.creatorProfile.data.ProfileRepository
import su.afk.kemonos.creatorProfile.domain.repository.ICreatorsRepository
import su.afk.kemonos.creatorProfile.domain.repository.IDiscordRepository
import su.afk.kemonos.creatorProfile.domain.repository.IFavoritesCreatorRepository
import su.afk.kemonos.creatorProfile.domain.repository.IProfileRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Binds
    @Singleton
    fun bindFavoritesCreatorRepository(impl: FavoritesCreatorRepository): IFavoritesCreatorRepository

    @Binds
    @Singleton
    fun bindCreatorsDataRepository(impl: CreatorsRepository): ICreatorsRepository

    @Binds
    @Singleton
    fun bindDiscordRepository(impl: DiscordRepository): IDiscordRepository

    @Binds
    @Singleton
    fun bindProfileRepository(impl: ProfileRepository): IProfileRepository
}
