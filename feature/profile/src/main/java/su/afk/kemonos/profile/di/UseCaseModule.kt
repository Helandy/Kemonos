package su.afk.kemonos.profile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.profile.api.domain.IGetAccountUseCase
import su.afk.kemonos.profile.api.domain.IIsCreatorFavoriteUseCase
import su.afk.kemonos.profile.api.domain.IIsPostFavoriteUseCase
import su.afk.kemonos.profile.api.domain.IRefreshFavoriteArtists
import su.afk.kemonos.profile.domain.account.GetAccountUseCase
import su.afk.kemonos.profile.domain.favorites.IsCreatorFavoriteUseCase
import su.afk.kemonos.profile.domain.favorites.IsPostFavoriteUseCase
import su.afk.kemonos.profile.domain.favorites.RefreshFavoriteArtists

@Module
@InstallIn(SingletonComponent::class)
internal interface UseCaseModule {

    @Binds
    fun bindIsCreatorFavoriteUseCase(impl: IsCreatorFavoriteUseCase): IIsCreatorFavoriteUseCase

    @Binds
    fun bindIsIsPostFavoriteUseCase(impl: IsPostFavoriteUseCase): IIsPostFavoriteUseCase

    @Binds
    fun bindIsGetAccountUseCase(impl: GetAccountUseCase): IGetAccountUseCase

    @Binds
    fun bindRefreshFavoriteArtists(impl: RefreshFavoriteArtists): IRefreshFavoriteArtists
}