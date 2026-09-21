package su.afk.kemonos.profile.di

import su.afk.kemonos.network.api.SiteRetrofitProvider
import su.afk.kemonos.domain.SelectedSite
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import su.afk.kemonos.network.di.ApiOkHttp
import su.afk.kemonos.profile.data.api.AccountApi
import su.afk.kemonos.profile.data.api.AuthenticationApi
import su.afk.kemonos.profile.data.api.FavoritesApi
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class PawchiveAuthOkHttp

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

    /**
     * Клиент для входа на pawchive.
     *
     * Общий клиент переписывает host каждого запроса под выбранный сайт и
     * добавляет свои заголовки — форме входа, которая ходит по абсолютному
     * адресу и читает Set-Cookie с 302, это ломает и адрес, и ответ.
     * Пул соединений при этом переиспользуем.
     */
    @Provides
    @Singleton
    @PawchiveAuthOkHttp
    fun providePawchiveAuthOkHttp(@ApiOkHttp base: OkHttpClient): OkHttpClient =
        base.newBuilder()
            .apply {
                interceptors().clear()
                networkInterceptors().clear()
            }
            .followRedirects(false)
            .followSslRedirects(false)
            .build()

    /** Экземпляры, привязанные к источнику. */
    @Provides
    @Singleton
    fun provideFavoritesApiBySite(
        provider: SiteRetrofitProvider,
    ): Map<SelectedSite, @JvmSuppressWildcards FavoritesApi> =
        provider.createApis(FavoritesApi::class.java)
}