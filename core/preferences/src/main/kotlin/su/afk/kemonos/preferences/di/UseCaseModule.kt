package su.afk.kemonos.preferences.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.preferences.GetCurrentSiteRootUrlUseCase
import su.afk.kemonos.preferences.IGetCurrentSiteRootUrlUseCase
import su.afk.kemonos.preferences.favoriteProfiles.FavoriteProfilesFiltersUseCase
import su.afk.kemonos.preferences.favoriteProfiles.IFavoriteProfilesFiltersUseCase
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.site.SelectedSiteUseCase
import su.afk.kemonos.preferences.siteUrl.GetFlowBaseUrlPrefsUseCase
import su.afk.kemonos.preferences.siteUrl.IGetBaseUrlsUseCase
import su.afk.kemonos.preferences.siteUrl.ISetBaseUrlsUseCase
import su.afk.kemonos.preferences.siteUrl.SetBaseUrlsUseCase
import su.afk.kemonos.preferences.ui.IUiSettingsReader
import su.afk.kemonos.preferences.ui.IUiSettingsWriter
import su.afk.kemonos.preferences.ui.UiSettingsRepository
import su.afk.kemonos.preferences.useCase.CacheTimestampUseCaseImpl
import su.afk.kemonos.preferences.useCase.ICacheTimestampUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface UseCaseModule {

    @Binds
    @Singleton
    fun bindCacheTimestampUseCase(impl: CacheTimestampUseCaseImpl): ICacheTimestampUseCase

    @Binds
    @Singleton
    fun bindSelectedSiteProvider(impl: SelectedSiteUseCase): ISelectedSiteUseCase

    @Binds
    fun bindSetBaseUrlsUseCase(impl: SetBaseUrlsUseCase): ISetBaseUrlsUseCase

    @Binds
    fun bindGetBaseUrlsUseCase(impl: GetFlowBaseUrlPrefsUseCase): IGetBaseUrlsUseCase

    @Binds
    fun bindGetCurrentSiteRootUrlUseCase(impl: GetCurrentSiteRootUrlUseCase): IGetCurrentSiteRootUrlUseCase

    @Binds
    fun bindUiSettingsReader(impl: UiSettingsRepository): IUiSettingsReader

    @Binds
    fun bindUiSettingsWriter(impl: UiSettingsRepository): IUiSettingsWriter

    @Binds
    @Singleton
    fun bindFavoriteProfilesFiltersUseCase(
        impl: FavoriteProfilesFiltersUseCase
    ): IFavoriteProfilesFiltersUseCase
}
