package su.afk.kemonos.preferences.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.preferences.GetCurrentSiteRootUrlUseCase
import su.afk.kemonos.preferences.IGetCurrentSiteRootUrlUseCase
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.site.SelectedSiteUseCase
import su.afk.kemonos.preferences.siteUrl.GetFlowBaseUrlPrefsUseCase
import su.afk.kemonos.preferences.siteUrl.IGetBaseUrlsUseCase
import su.afk.kemonos.preferences.siteUrl.ISetBaseUrlsUseCase
import su.afk.kemonos.preferences.siteUrl.SetBaseUrlsUseCase
import su.afk.kemonos.preferences.useCase.CacheTimestampUseCase
import su.afk.kemonos.preferences.useCase.CacheTimestampUseCaseImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface UseCaseModule {

    @Binds
    @Singleton
    fun bindCacheTimestampUseCase(impl: CacheTimestampUseCaseImpl): CacheTimestampUseCase

    @Binds
    @Singleton
    fun bindSelectedSiteProvider(impl: SelectedSiteUseCase): ISelectedSiteUseCase

    @Binds
    fun bindSetBaseUrlsUseCase(impl: SetBaseUrlsUseCase): ISetBaseUrlsUseCase

    @Binds
    fun bindGetBaseUrlsUseCase(impl: GetFlowBaseUrlPrefsUseCase): IGetBaseUrlsUseCase

    @Binds
    fun bindGetCurrentSiteRootUrlUseCase(impl: GetCurrentSiteRootUrlUseCase): IGetCurrentSiteRootUrlUseCase
}