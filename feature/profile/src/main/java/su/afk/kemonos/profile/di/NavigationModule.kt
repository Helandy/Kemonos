package su.afk.kemonos.profile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.profile.api.domain.IProfileNavigator
import su.afk.kemonos.profile.navigation.ProfileNavigator
import su.afk.kemonos.profile.presenter.favoritePosts.FavoritePostsNavigator
import su.afk.kemonos.profile.presenter.favoriteProfiles.FavoriteProfilesRegister
import su.afk.kemonos.profile.presenter.login.LoginNavigator
import su.afk.kemonos.profile.presenter.profile.ProfileNavigatorRegister
import su.afk.kemonos.profile.presenter.register.RegisterNavigator
import su.afk.kemonos.profile.presenter.setting.SettingNavigatorRegister
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {

    @Binds
    @IntoSet
    fun bindProfileProfile(impl: ProfileNavigatorRegister): NavRegistrar

    @Binds
    @IntoSet
    fun bindSettingNavigatorRegister(impl: SettingNavigatorRegister): NavRegistrar

    @Binds
    @IntoSet
    fun bindProfileLogin(impl: LoginNavigator): NavRegistrar

    @Binds
    @IntoSet
    fun bindRegisterNavigator(impl: RegisterNavigator): NavRegistrar

    @Binds
    @IntoSet
    fun bindFavoriteProfilesNavigator(impl: FavoriteProfilesRegister): NavRegistrar

    @Binds
    @IntoSet
    fun bindFavoritePostsNavigator(impl: FavoritePostsNavigator): NavRegistrar

    @Binds
    @Singleton
    fun bindProfileNavigator(impl: ProfileNavigator): IProfileNavigator
}