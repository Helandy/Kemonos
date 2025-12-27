package su.afk.kemonos.posts.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.posts.data.IPostsRepository
import su.afk.kemonos.posts.data.PostsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Binds
    @Singleton
    fun providePostsRepository(impl: PostsRepository): IPostsRepository
}