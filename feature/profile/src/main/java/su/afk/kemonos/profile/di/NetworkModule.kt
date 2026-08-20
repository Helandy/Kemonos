package su.afk.kemonos.profile.di

import su.afk.kemonos.network.api.SiteRetrofitProvider
import su.afk.kemonos.domain.SelectedSite
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import su.afk.kemonos.profile.data.api.AccountApi
import su.afk.kemonos.profile.data.api.AuthenticationApi
import su.afk.kemonos.profile.data.api.FavoritesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthenticationApi(retrofit: Retrofit): AuthenticationApi =
        retrofit.create(AuthenticationApi::class.java)

    @Provides
    @Singleton
    fun provideFavoritesApi(retrofit: Retrofit): FavoritesApi =
        retrofit.create(FavoritesApi::class.java)

    @Provides
    @Singleton
    fun provideAccountApi(retrofit: Retrofit): AccountApi =
        retrofit.create(AccountApi::class.java)

    /** Экземпляры, привязанные к источнику. */
    @Provides
    @Singleton
    fun provideFavoritesApiBySite(
        provider: SiteRetrofitProvider,
    ): Map<SelectedSite, @JvmSuppressWildcards FavoritesApi> =
        provider.createApis(FavoritesApi::class.java)
}