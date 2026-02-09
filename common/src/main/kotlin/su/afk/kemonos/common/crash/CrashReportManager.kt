package su.afk.kemonos.common.crash

import android.content.Context
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrashReportManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : ICrashReportManager {

    private var installed = false

    override fun install() {
        if (installed) return
        installed = true

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                saveCrash(thread, throwable)
            } catch (_: Throwable) {
                // ignore: never crash the crash handler itself
            } finally {
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }
    }

    override fun latestCrashPath(): String? {
        return crashDir()
            .listFiles()
            ?.filter { it.isFile && it.name.startsWith("crash_") && it.name.endsWith(".txt") }
            ?.maxByOrNull { it.lastModified() }
            ?.absolutePath
    }

    override fun deleteCrash(path: String) {
        runCatching { File(path).delete() }
    }

    private fun saveCrash(thread: Thread, throwable: Throwable) {
        val dir = crashDir()
        pruneOldCrashes(dir)

        val crashFile = File(dir, "crash_${System.currentTimeMillis()}.txt")
        crashFile.bufferedWriter().use { out ->
            val versionName = runCatching {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                packageInfo.versionName ?: "unknown"
            }.getOrElse { "unknown" }

            out.appendLine("Thread: ${thread.name}")
            out.appendLine("Time: ${Date()}")
            out.appendLine("App version: $versionName")
            out.appendLine("SDK: ${Build.VERSION.SDK_INT}")
            out.appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
            out.appendLine()
            out.appendLine(Log.getStackTraceString(throwable))
        }
    }

    private fun crashDir(): File {
        return File(context.filesDir, CRASH_DIR_NAME).apply { mkdirs() }
    }

    private fun pruneOldCrashes(dir: File) {
        val files = dir.listFiles()
            ?.filter { it.isFile && it.name.startsWith("crash_") && it.name.endsWith(".txt") }
            ?.sortedByDescending { it.lastModified() }
            ?: return

        files.drop(MAX_CRASH_FILES - 1).forEach { stale ->
            runCatching { stale.delete() }
        }
    }

    private companion object {
        private const val CRASH_DIR_NAME = "crashes"
        private const val MAX_CRASH_FILES = 5
    }
}
