package su.afk.kemonos.app.update.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.app.update.data.AppUpdateRepository
import su.afk.kemonos.app.update.data.IAppUpdateRepository

@Module
@InstallIn(SingletonComponent::class)
internal interface AppUpdateRepositoryModule {

    @Binds
    fun bindAppUpdateRepository(
        impl: AppUpdateRepository
    ): IAppUpdateRepository
}