package su.afk.kemonos.deepLink.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.deepLink.handler.DeepLinkHandler
import su.afk.kemonos.deepLink.handler.DeepLinkHandlerImpl
import su.afk.kemonos.deepLink.resolver.DeepLinkResolver
import su.afk.kemonos.deepLink.resolver.KemonosDeepLinkResolver
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DeepLinkModule {

    @Binds
    @Singleton
    fun bindDeepLinkResolver(impl: KemonosDeepLinkResolver): DeepLinkResolver

    @Binds
    @Singleton
    fun bindDeepLinkHandler(impl: DeepLinkHandlerImpl): DeepLinkHandler
}