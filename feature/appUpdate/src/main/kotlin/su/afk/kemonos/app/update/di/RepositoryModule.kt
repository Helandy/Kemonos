package su.afk.kemonos.app.update.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.app.update.data.AppUpdateRepository
import su.afk.kemonos.app.update.data.AppVersionProvider
import su.afk.kemonos.app.update.domain.repository.IAppUpdateRepository
import su.afk.kemonos.app.update.domain.repository.IAppVersionProvider

@Module
@InstallIn(SingletonComponent::class)
internal interface AppUpdateRepositoryModule {

    @Binds
    fun bindAppUpdateRepository(
        impl: AppUpdateRepository
    ): IAppUpdateRepository

    @Binds
    fun bindAppVersionProvider(
        impl: AppVersionProvider
    ): IAppVersionProvider
}
