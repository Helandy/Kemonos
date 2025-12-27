package su.afk.kemonos.creators.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import su.afk.kemonos.creators.ICreatorsNavigator
import su.afk.kemonos.creators.navigation.CreatorsNavigator
import su.afk.kemonos.creators.presenter.CreatorsNavigatorRegister
import su.afk.kemonos.navigation.NavRegistrar
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {

    @Binds
    @IntoSet
    fun bindCreatorsRegistrar(impl: CreatorsNavigatorRegister): NavRegistrar

    @Binds
    @Singleton
    fun bindCreatorsNavigator(impl: CreatorsNavigator): ICreatorsNavigator
}