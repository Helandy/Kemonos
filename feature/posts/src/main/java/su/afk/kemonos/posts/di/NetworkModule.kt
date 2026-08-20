package su.afk.kemonos.posts.di

import su.afk.kemonos.network.api.SiteRetrofitProvider
import su.afk.kemonos.domain.SelectedSite
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import su.afk.kemonos.posts.data.api.PostsApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun providePostsApi(retrofit: Retrofit): PostsApi =
        retrofit.create(PostsApi::class.java)

    /** Экземпляры, привязанные к источнику: для обращения не к текущему сайту. */
    @Provides
    @Singleton
    fun providePostsApiBySite(
        provider: SiteRetrofitProvider,
    ): Map<SelectedSite, @JvmSuppressWildcards PostsApi> =
        provider.createApis(PostsApi::class.java)
}