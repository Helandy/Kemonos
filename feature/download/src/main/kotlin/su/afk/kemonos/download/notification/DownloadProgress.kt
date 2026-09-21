package su.afk.kemonos.download.notification

import android.app.DownloadManager

/** Сколько файлов уже готово из тех, что сейчас в работе. */
internal data class DownloadProgress(
    val completed: Int,
    val failed: Int,
    val active: Int,
) {
    val total: Int get() = completed + failed + active

    /** Работы не осталось: показывать прогресс больше нечего. */
    val isFinished: Boolean get() = active == 0

    val hasAnything: Boolean get() = total > 0

    companion object {
        fun from(statuses: List<Int>): DownloadProgress {
            var completed = 0
            var failed = 0
            var active = 0

            statuses.forEach { status ->
                when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> completed++
                    DownloadManager.STATUS_FAILED -> failed++
                    DownloadManager.STATUS_PENDING,
                    DownloadManager.STATUS_RUNNING,
                    DownloadManager.STATUS_PAUSED,
                        -> active++
                }
            }

            return DownloadProgress(completed = completed, failed = failed, active = active)
        }
    }
}
