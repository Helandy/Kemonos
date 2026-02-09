package su.afk.kemonos.common.crash

interface ICrashReportManager {
    fun install()
    fun latestCrashPath(): String?
    fun deleteCrash(path: String)
}
