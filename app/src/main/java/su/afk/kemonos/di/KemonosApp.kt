package su.afk.kemonos.di

import android.app.Application
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import su.afk.kemonos.di.crash.CrashReportEntryPoint
import su.afk.kemonos.ui.shared.cleanupSharedMediaCache

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        cleanupSharedMediaCache(this)
        val crashEntryPoint = EntryPointAccessors.fromApplication(this, CrashReportEntryPoint::class.java)
        crashEntryPoint.crashReportManager().install()
    }
}
