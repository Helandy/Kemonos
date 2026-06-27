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
        AppMetricaInitializer.initialize(this, APPMETRICA_API_KEY)
        cleanupSharedMediaCache(this)
        val crashEntryPoint = EntryPointAccessors.fromApplication(this, CrashReportEntryPoint::class.java)
        crashEntryPoint.crashReportManager().install()
    }
}

private const val APPMETRICA_API_KEY = "ca4a25b2-8414-4738-8a71-206cbc9c9ad0"
