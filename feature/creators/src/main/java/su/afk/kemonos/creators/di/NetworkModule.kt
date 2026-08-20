package su.afk.kemonos.creators.di

import su.afk.kemonos.network.api.SiteRetrofitProvider
import su.afk.kemonos.domain.SelectedSite
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import su.afk.kemonos.creators.data.api.CreatorsApi
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideCreatorsApi(retrofit: Retrofit): CreatorsApi = retrofit.create(CreatorsApi::class.java)

    /** Экземпляры, привязанные к источнику: для обращения не к текущему сайту. */
    @Provides
    @Singleton
    fun provideCreatorsApiBySite(
        provider: SiteRetrofitProvider,
    ): Map<SelectedSite, @JvmSuppressWildcards CreatorsApi> =
        provider.createApis(CreatorsApi::class.java)
}