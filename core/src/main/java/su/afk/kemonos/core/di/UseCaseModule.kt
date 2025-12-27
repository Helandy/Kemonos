package su.afk.kemonos.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.core.api.domain.useCase.CacheTimestampUseCase
import su.afk.kemonos.core.api.domain.useCase.IGetBaseUrlsUseCase
import su.afk.kemonos.core.api.domain.useCase.ISelectedSiteUseCase
import su.afk.kemonos.core.api.domain.useCase.ISetBaseUrlsUseCase
import su.afk.kemonos.core.auth.AuthLocalDataSource
import su.afk.kemonos.core.auth.IAuthLocalDataSource
import su.afk.kemonos.core.domain.useCase.CacheTimestampUseCaseImpl
import su.afk.kemonos.core.preferences.GetCurrentSiteRootUrlUseCase
import su.afk.kemonos.core.preferences.IGetCurrentSiteRootUrlUseCase
import su.afk.kemonos.core.preferences.site.SelectedSiteUseCase
import su.afk.kemonos.core.preferences.siteUrl.GetFlowBaseUrlPrefsUseCase
import su.afk.kemonos.core.preferences.siteUrl.SetBaseUrlsUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface UseCaseModule {

    @Singleton
    @Binds
    fun bindCacheTimestampUseCase(impl: CacheTimestampUseCaseImpl): CacheTimestampUseCase

    @Singleton
    @Binds
    fun bindSelectedSiteProvider(impl: SelectedSiteUseCase): ISelectedSiteUseCase

    @Binds
    fun bindSetBaseUrlsUseCase(impl: SetBaseUrlsUseCase): ISetBaseUrlsUseCase

    @Binds
    fun bindGetBaseUrlsUseCase(impl: GetFlowBaseUrlPrefsUseCase): IGetBaseUrlsUseCase

    @Binds
    @Singleton
    fun provideAuthLocalDataSource(impl: AuthLocalDataSource): IAuthLocalDataSource

    @Binds
    fun bindGetCurrentSiteRootUrlUseCase(impl: GetCurrentSiteRootUrlUseCase): IGetCurrentSiteRootUrlUseCase
}