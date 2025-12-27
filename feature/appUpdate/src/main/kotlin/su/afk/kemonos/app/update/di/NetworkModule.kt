package su.afk.kemonos.app.update.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import su.afk.kemonos.app.update.data.api.GitHubReleasesApi
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    @Named("GitHubClient")
    fun provideGitHubClient(
        logging: HttpLoggingInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

    @Provides
    @Singleton
    @Named("GitHubRetrofit")
    fun provideGitHubRetrofit(
        @Named("GitHubClient") client: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideGitHubReleasesApi(
        @Named("GitHubRetrofit") retrofit: Retrofit
    ): GitHubReleasesApi = retrofit.create(GitHubReleasesApi::class.java)
}