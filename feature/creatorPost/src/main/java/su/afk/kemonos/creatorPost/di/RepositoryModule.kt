package su.afk.kemonos.creatorPost.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.creatorPost.data.repository.CommentsRepository
import su.afk.kemonos.creatorPost.data.repository.FavoritesPostRepository
import su.afk.kemonos.creatorPost.data.repository.FileRepository
import su.afk.kemonos.creatorPost.data.repository.PostRepository
import su.afk.kemonos.creatorPost.domain.repository.ICommentsRepository
import su.afk.kemonos.creatorPost.domain.repository.IFavoritesPostRepository
import su.afk.kemonos.creatorPost.domain.repository.IFileRepository
import su.afk.kemonos.creatorPost.domain.repository.IPostRepository
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

    @Binds
    @Singleton
    fun bindFileRepository(impl: FileRepository): IFileRepository
}
