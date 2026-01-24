package su.afk.kemonos.di.crash

import android.content.Context
import android.os.Build
import android.util.Log
import su.afk.kemonos.BuildConfig
import java.io.File
import java.util.*

/** Обдумать экран поделиться крашем */
object CrashLogger {

    fun install(context: Context) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                saveCrash(context, thread, throwable)
            } catch (_: Throwable) {
                // ничего, главное не зациклиться
            } finally {
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }
    }

    private fun saveCrash(
        context: Context,
        thread: Thread,
        throwable: Throwable
    ) {
        val dir = File(context.filesDir, "crashes").apply { mkdirs() }

        val file = File(
            dir,
            "crash_${System.currentTimeMillis()}.txt"
        )

        file.bufferedWriter().use { out ->
            out.appendLine("Thread: ${thread.name}")
            out.appendLine("Time: ${Date()}")
            out.appendLine("App version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            out.appendLine("SDK: ${Build.VERSION.SDK_INT}")
            out.appendLine()
            out.appendLine(Log.getStackTraceString(throwable))
        }
    }
}
