package su.afk.kemonos.download.domain.usecase

import android.app.DownloadManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import su.afk.kemonos.download.api.IDownloadUtil
import su.afk.kemonos.download.domain.model.DownloadManagerSnapshot
import su.afk.kemonos.download.domain.repository.DownloadManagerDataSource
import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository
import su.afk.kemonos.storage.api.repository.download.TrackedDownload

internal class DownloadActionUseCasesTest {

    @Test
    fun restartRemovesOldDownloadEnqueuesNewDownloadAndDeletesOldTrackedRow() = runBlocking {
        val dataSource = FakeDownloadManagerDataSource()
        val downloadUtil = FakeDownloadUtil(newDownloadId = 200L)
        val repository = FakeTrackedDownloadsRepository()
        val useCase = RestartDownloadUseCase(
            downloadManagerDataSource = dataSource,
            downloadUtil = downloadUtil,
            trackedDownloadsRepository = repository,
        )

        val newId = useCase(trackedDownload(id = 100L))

        assertEquals(200L, newId)
        assertEquals(listOf(100L), dataSource.removedIds)
        assertEquals(listOf(100L), repository.deletedIds)
        assertEquals("https://example.test/file.zip", downloadUtil.enqueued.single().url)
    }

    @Test
    fun restartDoesNotDeleteTrackedRowWhenDownloadManagerReusesId() = runBlocking {
        val dataSource = FakeDownloadManagerDataSource()
        val downloadUtil = FakeDownloadUtil(newDownloadId = 100L)
        val repository = FakeTrackedDownloadsRepository()
        val useCase = RestartDownloadUseCase(
            downloadManagerDataSource = dataSource,
            downloadUtil = downloadUtil,
            trackedDownloadsRepository = repository,
        )

        val newId = useCase(trackedDownload(id = 100L))

        assertEquals(100L, newId)
        assertEquals(listOf(100L), dataSource.removedIds)
        assertTrue(repository.deletedIds.isEmpty())
    }

    @Test
    fun restartAllRestartsEveryProvidedDownloadAndReturnsOldIds() = runBlocking {
        val dataSource = FakeDownloadManagerDataSource()
        val downloadUtil = FakeDownloadUtil(newDownloadId = 200L)
        val repository = FakeTrackedDownloadsRepository()
        val restartDownloadUseCase = RestartDownloadUseCase(
            downloadManagerDataSource = dataSource,
            downloadUtil = downloadUtil,
            trackedDownloadsRepository = repository,
        )
        val useCase = RestartDownloadsUseCase(restartDownloadUseCase)

        val restartedIds = useCase(
            listOf(
                trackedDownload(id = 100L),
                trackedDownload(id = 101L),
            )
        )

        assertEquals(listOf(100L, 101L), restartedIds)
        assertEquals(listOf(100L, 101L), dataSource.removedIds)
        assertEquals(listOf(100L, 101L), repository.deletedIds)
        assertEquals(2, downloadUtil.enqueued.size)
    }

    @Test
    fun stopRemovesSystemDownloadAndPersistsUserStoppedRuntimeState() = runBlocking {
        val dataSource = FakeDownloadManagerDataSource()
        val repository = FakeTrackedDownloadsRepository()
        val useCase = StopDownloadUseCase(
            downloadManagerDataSource = dataSource,
            trackedDownloadsRepository = repository,
        )

        useCase(100L)

        assertEquals(listOf(100L), dataSource.removedIds)
        val update = repository.runtimeUpdates.single()
        assertEquals(100L, update.downloadId)
        assertEquals(DownloadManager.STATUS_PAUSED, update.lastStatus)
        assertEquals(DownloadManager.PAUSED_UNKNOWN, update.lastReason)
        assertEquals(USER_STOPPED_LABEL, update.lastErrorLabel)
        assertTrue(update.lastSeenAtMs != null && update.lastSeenAtMs > 0L)
    }

    @Test
    fun deleteOnlyDeletesTrackedRow() = runBlocking {
        val repository = FakeTrackedDownloadsRepository()
        val useCase = DeleteDownloadUseCase(repository)

        useCase(100L)

        assertEquals(listOf(100L), repository.deletedIds)
        assertTrue(repository.runtimeUpdates.isEmpty())
    }

    @Test
    fun deleteAllDeletesEveryProvidedTrackedRow() = runBlocking {
        val repository = FakeTrackedDownloadsRepository()
        val useCase = DeleteDownloadsUseCase(repository)

        useCase(listOf(100L, 101L))

        assertEquals(listOf(100L, 101L), repository.deletedIds)
        assertTrue(repository.runtimeUpdates.isEmpty())
    }
}

private fun trackedDownload(id: Long): TrackedDownload =
    TrackedDownload(
        downloadId = id,
        url = "https://example.test/file.zip",
        fileName = "file.zip",
        service = "fanbox",
        creatorName = "Creator",
        postId = "post-1",
        postTitle = "Post",
        createdAtMs = 123L,
    )

private class FakeDownloadManagerDataSource : DownloadManagerDataSource {
    val removedIds = mutableListOf<Long>()

    override suspend fun querySnapshots(ids: List<Long>): Map<Long, DownloadManagerSnapshot> = emptyMap()

    override suspend fun remove(id: Long): Int {
        removedIds += id
        return 1
    }
}

private class FakeDownloadUtil(
    private val newDownloadId: Long,
) : IDownloadUtil {
    val enqueued = mutableListOf<EnqueuedDownload>()

    override suspend fun enqueueSystemDownload(
        url: String,
        fileName: String?,
        service: String?,
        creatorName: String?,
        postId: String?,
        postTitle: String?,
    ): Long {
        enqueued += EnqueuedDownload(
            url = url,
            fileName = fileName,
            service = service,
            creatorName = creatorName,
            postId = postId,
            postTitle = postTitle,
        )
        return newDownloadId
    }
}

private data class EnqueuedDownload(
    val url: String,
    val fileName: String?,
    val service: String?,
    val creatorName: String?,
    val postId: String?,
    val postTitle: String?,
)

private class FakeTrackedDownloadsRepository : ITrackedDownloadsRepository {
    val deletedIds = mutableListOf<Long>()
    val runtimeUpdates = mutableListOf<RuntimeUpdate>()

    override fun observeAll(): Flow<List<TrackedDownload>> = emptyFlow()

    override suspend fun upsert(item: TrackedDownload) = Unit

    override suspend fun delete(downloadId: Long) {
        deletedIds += downloadId
    }

    override suspend fun clearCache() = Unit

    override suspend fun updateRuntimeState(
        downloadId: Long,
        lastStatus: Int?,
        lastReason: Int?,
        lastErrorLabel: String?,
        lastSeenAtMs: Long?,
    ) {
        runtimeUpdates += RuntimeUpdate(
            downloadId = downloadId,
            lastStatus = lastStatus,
            lastReason = lastReason,
            lastErrorLabel = lastErrorLabel,
            lastSeenAtMs = lastSeenAtMs,
        )
    }
}

private data class RuntimeUpdate(
    val downloadId: Long,
    val lastStatus: Int?,
    val lastReason: Int?,
    val lastErrorLabel: String?,
    val lastSeenAtMs: Long?,
)
