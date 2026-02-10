package su.afk.kemonos.main.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import su.afk.kemonos.main.api.IMainNavigator
import su.afk.kemonos.main.navigation.MainNavigator
import su.afk.kemonos.main.presenter.MainNavigatorRegister
import su.afk.kemonos.navigation.NavRegistrar
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface MainNavigationModule {

    @Binds
    @IntoSet
    fun bindMainRegistrar(impl: MainNavigatorRegister): NavRegistrar

    @Binds
    @Singleton
    fun bindMainNavigator(impl: MainNavigator): IMainNavigator
}