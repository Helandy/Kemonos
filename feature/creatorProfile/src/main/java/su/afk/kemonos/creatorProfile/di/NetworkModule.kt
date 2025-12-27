package su.afk.kemonos.creatorProfile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import su.afk.kemonos.creatorProfile.data.api.CreatorProfileApi
import su.afk.kemonos.creatorProfile.data.api.FavoritesCreatorApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideFavoritesCreatorApi(retrofit: Retrofit): FavoritesCreatorApi =
        retrofit.create(FavoritesCreatorApi::class.java)

    @Provides
    @Singleton
    fun provideCreatorProfileApi(retrofit: Retrofit): CreatorProfileApi =
        retrofit.create(CreatorProfileApi::class.java)
}