package su.afk.kemonos.creatorPost.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import su.afk.kemonos.creatorPost.data.api.FavoritesPostApi
import su.afk.kemonos.creatorPost.data.api.FileApi
import su.afk.kemonos.creatorPost.data.api.PostsApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideFavoritesPostApi(retrofit: Retrofit): FavoritesPostApi =
        retrofit.create(FavoritesPostApi::class.java)

    @Provides
    @Singleton
    fun providePostsApi(retrofit: Retrofit): PostsApi =
        retrofit.create(PostsApi::class.java)

    @Provides
    @Singleton
    fun provideFileApi(retrofit: Retrofit): FileApi =
        retrofit.create(FileApi::class.java)
}
