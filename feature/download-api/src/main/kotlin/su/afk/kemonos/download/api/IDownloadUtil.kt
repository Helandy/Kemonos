package su.afk.kemonos.download.api

interface IDownloadUtil {
    /**
     * @return id задачи в DownloadManager (можно использовать для трекинга/проверки)
     */
    fun enqueueSystemDownload(
        url: String,
        fileName: String? = null,
        mimeType: String? = null,
    ): Long
}