package su.afk.kemonos.commonscreen.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import su.afk.kemonos.commonscreen.navigator.ErrorNavigator
import su.afk.kemonos.commonscreen.navigator.ErrorNavigatorRegister
import su.afk.kemonos.commonscreen.navigator.IErrorNavigator
import su.afk.kemonos.navigation.NavRegistrar
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {

    @Binds
    @IntoSet
    fun bindErrorNavigatorRegister(impl: ErrorNavigatorRegister): NavRegistrar

    @Binds
    @Singleton
    fun bindErrorNavigator(impl: ErrorNavigator): IErrorNavigator
}