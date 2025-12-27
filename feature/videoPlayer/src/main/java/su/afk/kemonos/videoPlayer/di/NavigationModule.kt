package su.afk.kemonos.videoPlayer.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.videoPlayer.navigation.VideoPlayerRegister

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {

    @Binds
    @IntoSet
    fun bindVideoPlayerRegister(impl: VideoPlayerRegister): NavRegistrar
}