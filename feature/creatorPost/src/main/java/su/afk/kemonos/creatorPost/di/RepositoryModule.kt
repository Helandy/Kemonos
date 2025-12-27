package su.afk.kemonos.creatorPost.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.creatorPost.data.repository.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Binds
    @Singleton
    fun bindFavoritesPostRepository(impl: FavoritesPostRepository): IFavoritesPostRepository

    @Binds
    @Singleton
    fun bindCommentsRepository(impl: CommentsRepository): ICommentsRepository

    @Binds
    @Singleton
    fun bindPostRepository(impl: PostRepository): IPostRepository
}