package su.afk.kemonos.download.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.download.api.IDownloadUtil
import su.afk.kemonos.download.util.DownloadUtil
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DownloadModule {

    @Binds
    @Singleton
    fun bindDownloadUtil(impl: DownloadUtil): IDownloadUtil
}