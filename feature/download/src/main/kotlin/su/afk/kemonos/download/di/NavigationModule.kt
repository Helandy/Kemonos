package su.afk.kemonos.download.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import su.afk.kemonos.download.api.IDownloadNavigator
import su.afk.kemonos.download.navigation.DownloadNavigator
import su.afk.kemonos.download.presenter.DownloadsNavigatorRegister
import su.afk.kemonos.navigation.NavRegistrar
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {

    @Binds
    @Singleton
    fun bindDownloadNavigator(impl: DownloadNavigator): IDownloadNavigator

    @Binds
    @IntoSet
    fun bindDownloadNavRegister(impl: DownloadsNavigatorRegister): NavRegistrar
}
