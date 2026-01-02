package su.afk.kemonos.common.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.common.repository.checkApi.CheckApiRepository
import su.afk.kemonos.common.repository.checkApi.ICheckApiRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Singleton
    @Binds
    fun bindCheckApiRepository(
        impl: CheckApiRepository
    ): ICheckApiRepository
}