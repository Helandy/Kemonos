package su.afk.kemonos.creatorPost.domain.download

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.creatorPost.api.download.BatchDownloadPost
import su.afk.kemonos.creatorPost.api.download.BatchDownloadRequest
import su.afk.kemonos.creatorPost.domain.repository.IPostRepository
import su.afk.kemonos.creatorPost.domain.useCase.GetPostUseCase
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.AttachmentDomain
import su.afk.kemonos.domain.models.FileDomain
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.download.api.IDownloadUtil
import su.afk.kemonos.preferences.domainResolver.IDomainResolver
import su.afk.kemonos.preferences.domainResolver.SiteHostConfig

internal class PostsBatchDownloaderTest {

    @Test
    fun `numbers files across posts in the selected order`() = runBlocking {
        val downloadUtil = FakeDownloadUtil()
        val downloader = downloader(
            posts = mapOf(
                "post-a" to postWith(
                    id = "post-a",
                    attachments = listOf(attachment("/a/1/aaa.png"), attachment("/a/2/bbb.png")),
                ),
                "post-b" to postWith(
                    id = "post-b",
                    attachments = listOf(attachment("/b/1/ccc.jpg")),
                ),
            ),
            downloadUtil = downloadUtil,
        )

        val result = downloader.enqueue(request(listOf("post-a", "post-b")))

        assertEquals(listOf("1.png", "2.png", "3.jpg"), downloadUtil.enqueued.map { it.fileName })
        assertEquals(3, result.enqueuedFiles)
        assertEquals(2, result.loadedPosts)
        assertEquals(0, result.failedPosts)
    }

    @Test
    fun `selection order decides the numbering`() = runBlocking {
        val downloadUtil = FakeDownloadUtil()
        val downloader = downloader(
            posts = mapOf(
                "post-a" to postWith(id = "post-a", attachments = listOf(attachment("/a/1/aaa.png"))),
                "post-b" to postWith(id = "post-b", attachments = listOf(attachment("/b/1/ccc.jpg"))),
            ),
            downloadUtil = downloadUtil,
        )

        downloader.enqueue(request(listOf("post-b", "post-a")))

        assertEquals(listOf("1.jpg", "2.png"), downloadUtil.enqueued.map { it.fileName })
    }

    @Test
    fun `all files of one batch land in the same folder`() = runBlocking {
        val downloadUtil = FakeDownloadUtil()
        val downloader = downloader(
            posts = mapOf(
                "post-a" to postWith(id = "post-a", attachments = listOf(attachment("/a/1/aaa.png"))),
                "post-b" to postWith(id = "post-b", attachments = listOf(attachment("/b/1/ccc.jpg"))),
            ),
            downloadUtil = downloadUtil,
        )

        downloader.enqueue(request(listOf("post-a", "post-b")))

        assertEquals(listOf(FOLDER, FOLDER), downloadUtil.enqueued.map { it.subDir })
    }

    @Test
    fun `covers are skipped for every post when disabled`() = runBlocking {
        val downloadUtil = FakeDownloadUtil()
        val cover = attachment("/a/0/cover.png")
        val downloader = downloader(
            posts = mapOf(
                "post-a" to postWith(
                    id = "post-a",
                    attachments = listOf(cover, attachment("/a/1/aaa.png")),
                    coverPath = cover.path,
                ),
                "post-b" to postWith(
                    id = "post-b",
                    attachments = listOf(attachment("/b/1/ccc.jpg")),
                ),
            ),
            downloadUtil = downloadUtil,
        )

        downloader.enqueue(request(listOf("post-a", "post-b"), includeCovers = false))

        assertEquals(listOf("1.png", "2.jpg"), downloadUtil.enqueued.map { it.fileName })
        assertEquals(
            listOf("https://file.test/data/a/1/aaa.png", "https://file.test/data/b/1/ccc.jpg"),
            downloadUtil.enqueued.map { it.url.substringBefore('?') },
        )
    }

    @Test
    fun `a post that fails to load does not break the numbering of the rest`() = runBlocking {
        val downloadUtil = FakeDownloadUtil()
        val downloader = downloader(
            posts = mapOf(
                "post-b" to postWith(id = "post-b", attachments = listOf(attachment("/b/1/ccc.jpg"))),
            ),
            downloadUtil = downloadUtil,
        )

        val result = downloader.enqueue(request(listOf("missing", "post-b")))

        assertEquals(listOf("1.jpg"), downloadUtil.enqueued.map { it.fileName })
        assertEquals(1, result.loadedPosts)
        assertEquals(1, result.failedPosts)
    }

    private fun downloader(
        posts: Map<String, PostContentDomain>,
        downloadUtil: IDownloadUtil,
    ) = PostsBatchDownloader(
        getPost = GetPostUseCase(FakePostRepository(posts)),
        domainResolver = FakeDomainResolver(),
        downloadUtil = downloadUtil,
    )

    private fun request(
        postIds: List<String>,
        includeCovers: Boolean = true,
    ) = BatchDownloadRequest(
        service = SERVICE,
        creatorId = CREATOR_ID,
        creatorName = "Creator",
        posts = postIds.map { BatchDownloadPost(postId = it, title = it) },
        folderName = FOLDER,
        includeCovers = includeCovers,
    )

    private fun attachment(path: String) = AttachmentDomain(
        server = FILE_BASE_URL,
        path = path,
        name = path.substringAfterLast('/'),
    )

    private fun postWith(
        id: String,
        attachments: List<AttachmentDomain>,
        coverPath: String? = null,
    ) = PostContentDomain(
        post = PostDomain.default().copy(
            id = id,
            userId = CREATOR_ID,
            service = SERVICE,
            attachments = emptyList(),
            file = coverPath?.let { FileDomain(name = it.substringAfterLast('/'), path = it) },
        ),
        attachments = attachments,
    )

    private companion object {
        const val SERVICE = "patreon"
        const val CREATOR_ID = "creator-1"
        const val FOLDER = "Creator_20260826_1200"
        const val FILE_BASE_URL = "https://file.test"
    }
}

private class FakePostRepository(
    private val posts: Map<String, PostContentDomain>,
) : IPostRepository {
    override suspend fun getPost(service: String, id: String, postId: String): PostContentDomain? =
        posts[postId]

    override suspend fun getPostRevision(
        service: String,
        id: String,
        postId: String,
        revisionId: Long,
    ): PostContentDomain? = null
}

private class FakeDomainResolver : IDomainResolver {
    override fun selectedSite(): SelectedSite = SelectedSite.P
    override fun baseUrlByService(service: String): String = "https://pawchive.test"
    override fun imageBaseUrlByService(service: String): String = "https://img.test"
    override fun creatorImageBaseUrlByService(service: String): String = "https://pawchive.test"
    override fun fileBaseUrlByService(service: String): String = "https://file.test"
    override fun hostConfig(site: SelectedSite): SiteHostConfig = SiteHostConfig(
        apiBaseUrl = "https://pawchive.test/api/",
        rootUrl = "https://pawchive.test",
        imageBaseUrl = "https://img.test",
        fileBaseUrl = "https://file.test",
        creatorImageBaseUrl = "https://pawchive.test",
    )
}

private class FakeDownloadUtil : IDownloadUtil {
    val enqueued = mutableListOf<Enqueued>()

    override suspend fun enqueueSystemDownload(
        url: String,
        fileName: String?,
        service: String?,
        creatorName: String?,
        postId: String?,
        postTitle: String?,
        subDir: String?,
    ): Long {
        enqueued += Enqueued(url = url, fileName = fileName, subDir = subDir)
        return enqueued.size.toLong()
    }
}

private data class Enqueued(
    val url: String,
    val fileName: String?,
    val subDir: String?,
)
