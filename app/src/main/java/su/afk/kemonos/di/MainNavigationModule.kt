package su.afk.kemonos.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.presenter.main.MainNavigator

@Module
@InstallIn(SingletonComponent::class)
interface MainNavigationModule {

    @Binds
    @IntoSet
    fun bindMainNavigator(impl: MainNavigator): NavRegistrar
}