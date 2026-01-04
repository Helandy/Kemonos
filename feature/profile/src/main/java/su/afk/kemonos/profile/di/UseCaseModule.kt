package su.afk.kemonos.profile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.profile.api.domain.*
import su.afk.kemonos.profile.domain.account.GetAccountUseCase
import su.afk.kemonos.profile.domain.favorites.*

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
    fun bindGetFavoriteArtistsUseCase(impl: GetFavoriteArtistsUseCase): IGetFavoriteArtistsUseCase

    @Binds
    fun bindComputeFreshFavoriteArtistsUpdatesUseCase(
        impl: ComputeFreshFavoriteArtistsUpdatesUseCase
    ): IComputeFreshFavoriteArtistsUpdatesUseCase

    @Binds
    fun bindRefreshFavoriteArtists(impl: RefreshFavoriteArtists): IRefreshFavoriteArtists
}