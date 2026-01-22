package su.afk.kemonos.creatorPost.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import su.afk.kemonos.creatorPost.api.ICreatorPostNavigator
import su.afk.kemonos.creatorPost.navigation.CreatorPostDestNavigator
import su.afk.kemonos.creatorPost.presenter.CreatorPostNavigatorRegister
import su.afk.kemonos.navigation.NavRegistrar
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {

    @Binds
    @IntoSet
    fun bindCreatorPostNavigator(impl: CreatorPostNavigatorRegister): NavRegistrar

    @Binds
    @Singleton
    fun bindCreatorPostDestNavigator(impl: CreatorPostDestNavigator): ICreatorPostNavigator
}