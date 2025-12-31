package su.afk.kemonos.main.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import su.afk.kemonos.main.presenter.MainNavigator
import su.afk.kemonos.navigation.NavRegistrar

@Module
@InstallIn(SingletonComponent::class)
interface MainNavigationModule {

    @Binds
    @IntoSet
    fun bindMainNavigator(impl: MainNavigator): NavRegistrar
}