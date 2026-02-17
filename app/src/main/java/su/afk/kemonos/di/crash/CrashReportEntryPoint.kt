package su.afk.kemonos.di.crash

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.ui.crash.ICrashReportManager

@EntryPoint
@InstallIn(SingletonComponent::class)
interface CrashReportEntryPoint {
    fun crashReportManager(): ICrashReportManager
}
