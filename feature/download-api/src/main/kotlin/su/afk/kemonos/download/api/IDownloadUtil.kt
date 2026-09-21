package su.afk.kemonos.download.api

interface IDownloadUtil {
    /**
     * @return id задачи в DownloadManager (можно использовать для трекинга/проверки)
     */
    suspend fun enqueueSystemDownload(
        url: String,
        fileName: String?,
        service: String? = null,
        creatorName: String? = null,
        postId: String? = null,
        postTitle: String? = null,
        /**
         * Папка вместо той, что собрана по настройкам.
         *
         * Массовая загрузка складывает выбранные посты в одну папку, иначе
         * сквозная нумерация файлов разъехалась бы по папкам постов.
         */
        subDir: String? = null,
    ): Long
}
