package su.afk.kemonos.ui.crash

interface ICrashReportManager {
    fun install()
    fun latestCrashPath(): String?
    fun deleteCrash(path: String)
}
