package su.afk.kemonos.navigation.di

import androidx.navigation3.runtime.NavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.api.IPostsNavigator
import su.afk.kemonos.creators.ICreatorsNavigator
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.tab.BottomTab
import su.afk.kemonos.profile.api.domain.IProfileNavigator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {

    @Provides
    fun provideInitialTab(): BottomTab = BottomTab.CREATORS

    @Provides
    fun provideTabRoots(
        creatorsNavigator: ICreatorsNavigator,
        postsNavigator: IPostsNavigator,
        profileNavigator: IProfileNavigator,
    ): @JvmSuppressWildcards Map<BottomTab, NavKey> = mapOf(
        BottomTab.CREATORS to creatorsNavigator.getCreatorsDest(),
        BottomTab.POSTS to postsNavigator.getPostsDest(),
        BottomTab.PROFILE to profileNavigator.getProfileDest(),
    )

    @Provides
    @Singleton
    fun provideNavigationManager(
        roots: @JvmSuppressWildcards Map<BottomTab, NavKey>,
        initialTab: BottomTab,
    ): NavigationManager = NavigationManager(
        roots = roots,
        initialTab = initialTab,
    )
}