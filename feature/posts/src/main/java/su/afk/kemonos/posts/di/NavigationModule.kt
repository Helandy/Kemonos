package su.afk.kemonos.posts.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import su.afk.kemonos.api.IPostsNavigator
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.posts.navigation.PostsNavigator
import su.afk.kemonos.posts.presenter.pager.PostsPagerNavigator
import su.afk.kemonos.posts.presenter.tagsSelect.NavigatorTagsSelect
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {

    @Binds
    @IntoSet
    fun bindPostsPagerNavigator(impl: PostsPagerNavigator): NavRegistrar

    @Binds
    @IntoSet
    fun bindNavigatorTagsSelect(impl: NavigatorTagsSelect): NavRegistrar

    @Binds
    @Singleton
    fun bindPostsNavigator(impl: PostsNavigator): IPostsNavigator
}