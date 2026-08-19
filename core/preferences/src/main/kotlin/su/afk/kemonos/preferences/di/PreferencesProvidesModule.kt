package su.afk.kemonos.preferences.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.preferences.GetRootUrlUseCase
import su.afk.kemonos.preferences.UrlPrefs
import su.afk.kemonos.preferences.domainResolver.DomainResolver
import su.afk.kemonos.preferences.domainResolver.IDomainResolver
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesProvidesModule {

    @Provides
    @Singleton
    fun provideDomainResolver(
        getRootUrl: GetRootUrlUseCase,
        selectedSiteUseCase: ISelectedSiteUseCase,
        urlPrefs: UrlPrefs,
    ): IDomainResolver = DomainResolver(
        getRootUrl,
        selectedSiteUseCase,
        urlPrefs,
    )
}
