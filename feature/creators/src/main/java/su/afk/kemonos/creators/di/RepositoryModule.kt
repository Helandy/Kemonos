package su.afk.kemonos.creators.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.creators.data.CreatorsRepository
import su.afk.kemonos.creators.data.ICreatorsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Singleton
    @Binds
    fun bindCreatorsRepository(impl: CreatorsRepository): ICreatorsRepository
}