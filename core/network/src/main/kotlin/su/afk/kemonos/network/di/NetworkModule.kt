package su.afk.kemonos.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import su.afk.kemonos.network.BuildConfig
import su.afk.kemonos.network.api.BaseUrlProvider
import su.afk.kemonos.network.api.FlowBaseUrlProvider
import su.afk.kemonos.network.api.ReplaceBaseUrlInterceptor
import su.afk.kemonos.network.api.SwitchingBaseUrlProvider
import su.afk.kemonos.network.auth.AuthCookieInterceptor
import su.afk.kemonos.network.textInterceptor.TextInterceptor
import su.afk.kemonos.network.versionInterceptor.VersionInterceptor
import su.afk.kemonos.preferences.UrlPrefs
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.preferences.ui.UiSettingModel
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
    @Named("VideoPreviewServerBaseUrlProvider")
    fun provideVideoPreviewServerBaseUrlProvider(
        @Named("AppScope") scope: CoroutineScope,
        uiSettingUseCase: IUiSettingUseCase,
    ): BaseUrlProvider = FlowBaseUrlProvider(
        scope = scope,
        initialUrl = UiSettingModel.DEFAULT_VIDEO_PREVIEW_SERVER_URL,
        urlFlow = uiSettingUseCase.prefs
            .map { it.videoPreviewServerUrl }
            .distinctUntilChanged(),
    )

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
        HttpLoggingInterceptor().apply {
            redactHeader("Cookie")
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    /** Для Self запроса инфы о видео */
    @Provides
    @Singleton
    @Named("VideoInfoClient")
    fun provideVideoInfoClient(
        logging: HttpLoggingInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    @Named("VideoInfoClientRemote")
    fun provideVideoInfoClientRemote(
        @Named("VideoPreviewServerBaseUrlProvider") baseUrlProvider: BaseUrlProvider,
        logging: HttpLoggingInterceptor,
        versionInterceptor: VersionInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(ReplaceBaseUrlInterceptor(baseUrlProvider))
        .addInterceptor(versionInterceptor)
        .addInterceptor(logging)
        .callTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    @Named("VideoInfoRetrofitRemote")
    fun provideVideoInfoRetrofitRemote(
        @Named("VideoInfoClientRemote") client: OkHttpClient,
    ): Retrofit = Retrofit.Builder()
        .baseUrl("https://placeholder/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

}
