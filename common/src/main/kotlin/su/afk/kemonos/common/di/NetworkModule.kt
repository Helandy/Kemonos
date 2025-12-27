package su.afk.kemonos.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import su.afk.kemonos.common.api.KemonoApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun providePostsApi(retrofit: Retrofit): KemonoApi = retrofit.create(KemonoApi::class.java)
}