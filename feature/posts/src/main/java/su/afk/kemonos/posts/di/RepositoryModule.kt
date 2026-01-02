package su.afk.kemonos.posts.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.posts.data.CheckApiRepository
import su.afk.kemonos.posts.data.ICheckApiRepository
import su.afk.kemonos.posts.data.IPostsRepository
import su.afk.kemonos.posts.data.PostsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Binds
    @Singleton
    fun providePostsRepository(impl: PostsRepository): IPostsRepository

    @Binds
    @Singleton
    fun provideCheckApiRepository(impl: CheckApiRepository): ICheckApiRepository
}