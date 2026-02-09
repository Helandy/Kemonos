package su.afk.kemonos.di

import android.app.Application
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import su.afk.kemonos.di.crash.CrashReportEntryPoint

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val crashEntryPoint = EntryPointAccessors.fromApplication(this, CrashReportEntryPoint::class.java)
        crashEntryPoint.crashReportManager().install()
    }
}
