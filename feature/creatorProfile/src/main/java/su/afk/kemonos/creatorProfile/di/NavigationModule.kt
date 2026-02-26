package su.afk.kemonos.creatorProfile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.creatorProfile.navigation.CreatorProfileNavigator
import su.afk.kemonos.creatorProfile.presenter.communityChat.CommunityChatRegister
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.CreatorProfileRegister
import su.afk.kemonos.navigation.NavRegistrar
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {

    @Binds
    @IntoSet
    fun bindCreatorProfile(impl: CreatorProfileRegister): NavRegistrar

    @Binds
    @IntoSet
    fun bindCommunityChatRegister(impl: CommunityChatRegister): NavRegistrar

    @Binds
    @Singleton
    fun bindCreatorProfileDest(impl: CreatorProfileNavigator): ICreatorProfileNavigator
}