package su.afk.kemonos.creators.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import su.afk.kemonos.creators.data.api.CreatorsApi
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideCreatorsApi(retrofit: Retrofit): CreatorsApi = retrofit.create(CreatorsApi::class.java)
}