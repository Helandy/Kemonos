package su.afk.kemonos.creatorPost.domain.download

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import su.afk.kemonos.creatorPost.api.download.BatchDownloadRequest
import su.afk.kemonos.creatorPost.api.download.BatchDownloadResult
import su.afk.kemonos.creatorPost.api.download.IPostsBatchDownloader
import su.afk.kemonos.creatorPost.domain.useCase.GetPostUseCase
import su.afk.kemonos.download.api.IDownloadUtil
import su.afk.kemonos.preferences.domainResolver.IDomainResolver
import su.afk.kemonos.preferences.domainResolver.mediaUrlSchemeByService
import su.afk.kemonos.utils.download.numberedFileName
import su.afk.kemonos.utils.withIo
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ставит в очередь файлы нескольких постов одной пачкой.
 *
 * Посты обрабатываются строго по одному в порядке выбора, а внутри поста файлы
 * идут в том же порядке, что и на странице: только так сквозная нумерация
 * `1.png, 2.png, ...` совпадает с тем, что пользователь видел глазами.
 */
@Singleton
internal class PostsBatchDownloader @Inject constructor(
    private val getPost: GetPostUseCase,
    private val domainResolver: IDomainResolver,
    private val downloadUtil: IDownloadUtil,
) : IPostsBatchDownloader {

    /** Живёт столько же, сколько процесс: пачку не должно ронять закрытие экрана. */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun start(request: BatchDownloadRequest) {
        scope.launch { runCatching { enqueue(request) } }
    }

    override suspend fun enqueue(request: BatchDownloadRequest): BatchDownloadResult = withIo {
        val fallbackBaseUrl = domainResolver.fileBaseUrlByService(request.service)
        val mediaUrlScheme = domainResolver.mediaUrlSchemeByService(request.service)

        var number = 0
        var loadedPosts = 0
        var failedPosts = 0

        request.posts.forEach { post ->
            val content = runCatching {
                getPost(
                    service = request.service,
                    id = request.creatorId,
                    postId = post.postId,
                )
            }.getOrNull()

            if (content == null) {
                failedPosts++
                return@forEach
            }

            loadedPosts++

            content.collectDownloadAllItems(
                fallbackBaseUrl = fallbackBaseUrl,
                mediaUrlScheme = mediaUrlScheme,
                includeCover = request.includeCovers,
            ).forEach { item ->
                number++
                downloadUtil.enqueueSystemDownload(
                    url = item.url,
                    fileName = numberedFileName(
                        number = number,
                        sourceName = item.fileName,
                        sourceUrl = item.url,
                    ),
                    service = request.service,
                    creatorName = request.creatorName,
                    postId = post.postId,
                    postTitle = post.title,
                    subDir = request.folderName,
                )
            }
        }

        BatchDownloadResult(
            enqueuedFiles = number,
            loadedPosts = loadedPosts,
            failedPosts = failedPosts,
        )
    }
}
