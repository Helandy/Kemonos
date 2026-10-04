package su.afk.kemonos.download.domain.model

/** Значения совпадают с [android.app.DownloadManager] (стабильный публичный API), чтобы domain не зависел от Android. */
internal object DownloadStatusCode {
    const val PAUSED = 4
    const val PAUSED_UNKNOWN = 4
}
