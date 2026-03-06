package su.afk.kemonos.profile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.profile.api.domain.IGetProfileDestinationUseCase
import su.afk.kemonos.profile.navigation.GetGetProfileDestinationUseCase
import su.afk.kemonos.profile.presenter.blacklist.AuthorsBlacklistNavigatorRegister
import su.afk.kemonos.profile.presenter.faq.FaqNavigatorRegister
import su.afk.kemonos.profile.presenter.favoritePosts.FavoritePostsNavigatorRegister
import su.afk.kemonos.profile.presenter.favoriteProfiles.FavoriteProfilesRegister
import su.afk.kemonos.profile.presenter.importResult.ImportResultNavigatorRegister
import su.afk.kemonos.profile.presenter.login.LoginNavigatorRegister
import su.afk.kemonos.profile.presenter.profile.ProfileNavigatorRegister
import su.afk.kemonos.profile.presenter.register.RegisterNavigatorRegister
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {

    @Binds
    @IntoSet
    fun bindProfileProfile(impl: ProfileNavigatorRegister): NavRegistrar

    @Binds
    @IntoSet
    fun bindProfileLogin(impl: LoginNavigatorRegister): NavRegistrar

    @Binds
    @IntoSet
    fun bindRegisterNavigator(impl: RegisterNavigatorRegister): NavRegistrar

    @Binds
    @IntoSet
    fun bindFavoriteProfilesNavigator(impl: FavoriteProfilesRegister): NavRegistrar

    @Binds
    @IntoSet
    fun bindFavoritePostsNavigator(impl: FavoritePostsNavigatorRegister): NavRegistrar

    @Binds
    @IntoSet
    fun bindAuthorsBlacklistNavigator(impl: AuthorsBlacklistNavigatorRegister): NavRegistrar

    @Binds
    @IntoSet
    fun bindFaqNavigator(impl: FaqNavigatorRegister): NavRegistrar

    @Binds
    @IntoSet
    fun bindImportResultNavigator(impl: ImportResultNavigatorRegister): NavRegistrar

    @Binds
    @Singleton
    fun bindProfileNavigator(impl: GetGetProfileDestinationUseCase): IGetProfileDestinationUseCase
}
