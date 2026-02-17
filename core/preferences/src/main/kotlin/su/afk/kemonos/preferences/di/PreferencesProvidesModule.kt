package su.afk.kemonos.preferences.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.preferences.GetCoomerRootUrlUseCase
import su.afk.kemonos.preferences.GetKemonoRootUrlUseCase
import su.afk.kemonos.preferences.domainResolver.DomainResolver
import su.afk.kemonos.preferences.domainResolver.IDomainResolver
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesProvidesModule {

    @Provides
    @Singleton
    fun provideDomainResolver(
        getKemonoRootUrl: GetKemonoRootUrlUseCase,
        getCoomerRootUrl: GetCoomerRootUrlUseCase,
    ): IDomainResolver = DomainResolver(getKemonoRootUrl, getCoomerRootUrl)
}