package su.afk.kemonos.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import su.afk.kemonos.network.api.BaseUrlProvider
import su.afk.kemonos.network.api.ReplaceBaseUrlInterceptor
import su.afk.kemonos.network.api.SwitchingBaseUrlProvider
import su.afk.kemonos.network.auth.AuthCookieInterceptor
import su.afk.kemonos.network.textInterceptor.TextInterceptor
import su.afk.kemonos.preferences.UrlPrefs
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiOkHttp

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideBaseUrlProvider(
        @Named("AppScope") scope: CoroutineScope,
        prefs: UrlPrefs
    ): BaseUrlProvider = SwitchingBaseUrlProvider(scope, prefs)

    @Provides
    @Singleton
    @ApiOkHttp
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        baseProvider: BaseUrlProvider,
        authCookieInterceptor: AuthCookieInterceptor,
        creatorsTextInterceptor: TextInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(ReplaceBaseUrlInterceptor(baseProvider))
        .addInterceptor(authCookieInterceptor)
        .addInterceptor(creatorsTextInterceptor)
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        @ApiOkHttp client: OkHttpClient
    ): Retrofit = Retrofit.Builder()
            .baseUrl("https://placeholder/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }


    /** Для запроса инфы о видео */
    @Provides
    @Named("VideoInfoClient")
    fun provideVideoInfoClient(
        @ApiOkHttp client: OkHttpClient
    ): OkHttpClient {
        return client
    }
}