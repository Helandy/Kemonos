package su.afk.kemonos.creatorPost.api.download

/** Пост в очереди массовой загрузки. Позиция в списке — это порядок, выбранный пользователем. */
data class BatchDownloadPost(
    val postId: String,
    val title: String?,
)

/**
 * Скачать несколько постов в одну папку со сквозной нумерацией файлов.
 *
 * Файлы получают номера подряд: страницы первого поста, затем второго и так далее,
 * поэтому порядок постов и порядок страниц внутри поста должны сохраняться.
 */
data class BatchDownloadRequest(
    val service: String,
    val creatorId: String,
    val creatorName: String?,
    val posts: List<BatchDownloadPost>,
    /** Папка внутри каталога загрузок приложения. */
    val folderName: String,
    /** Общий ответ на «качать ли обложки» сразу для всех выбранных постов. */
    val includeCovers: Boolean,
)

data class BatchDownloadResult(
    val enqueuedFiles: Int,
    val loadedPosts: Int,
    val failedPosts: Int,
)

interface IPostsBatchDownloader {
    /**
     * Ставит пачку в очередь и ждёт, пока все посты будут разобраны.
     */
    suspend fun enqueue(request: BatchDownloadRequest): BatchDownloadResult

    /**
     * То же самое, но не привязано к вызывающему экрану.
     *
     * Разбор пачки — это сетевые запросы за каждым постом, и на полусотне работ
     * они идут минутами. Привязанные к экрану, они обрывались бы на середине,
     * стоит уйти с профиля автора.
     */
    fun start(request: BatchDownloadRequest)
}
