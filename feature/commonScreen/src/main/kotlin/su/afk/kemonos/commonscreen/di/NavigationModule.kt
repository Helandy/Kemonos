package su.afk.kemonos.commonscreen.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import su.afk.kemonos.commonscreen.errorScreen.ErrorNavigator
import su.afk.kemonos.commonscreen.errorScreen.ErrorNavigatorRegister
import su.afk.kemonos.commonscreen.errorScreen.domain.ImageViewNavigator
import su.afk.kemonos.commonscreen.imageViewScreen.ImageViewRegistrar
import su.afk.kemonos.commonscreen.navigator.IErrorNavigator
import su.afk.kemonos.commonscreen.navigator.IImageViewNavigator
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

    @Binds
    @IntoSet
    fun bindImageViewRegistrar(impl: ImageViewRegistrar): NavRegistrar

    @Binds
    @Singleton
    fun bindImageViewNavigator(impl: ImageViewNavigator): IImageViewNavigator
}