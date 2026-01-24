package su.afk.kemonos.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import su.afk.kemonos.common.presenter.webView.WebViewPool

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

//        CrashLogger.install(this)

        WebViewPool.prewarm(
            context = this,
            bgColor = android.graphics.Color.TRANSPARENT
        )
    }
}