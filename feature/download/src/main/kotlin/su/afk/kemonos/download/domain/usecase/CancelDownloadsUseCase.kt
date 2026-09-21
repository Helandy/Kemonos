package su.afk.kemonos.download.domain.usecase

import su.afk.kemonos.download.domain.repository.DownloadManagerDataSource
import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository
import su.afk.kemonos.utils.withIo
import javax.inject.Inject

/**
 * Отмена: в отличие от остановки, запись не остаётся — возобновлять нечего.
 *
 * Из DownloadManager задачу тоже убираем, иначе он продолжит качать файл,
 * которого в списке приложения уже нет.
 */
internal class CancelDownloadsUseCase @Inject constructor(
    private val downloadManagerDataSource: DownloadManagerDataSource,
    private val trackedDownloadsRepository: ITrackedDownloadsRepository,
) {
    suspend operator fun invoke(downloadIds: List<Long>) = withIo {
        downloadIds.forEach { downloadId ->
            downloadManagerDataSource.remove(downloadId)
            trackedDownloadsRepository.delete(downloadId)
        }
    }
}
