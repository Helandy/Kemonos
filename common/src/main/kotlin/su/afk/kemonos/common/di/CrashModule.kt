package su.afk.kemonos.common.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.common.crash.CrashReportManager
import su.afk.kemonos.common.crash.ICrashReportManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CrashModule {

    @Binds
    @Singleton
    fun bindCrashReportManager(impl: CrashReportManager): ICrashReportManager
}
