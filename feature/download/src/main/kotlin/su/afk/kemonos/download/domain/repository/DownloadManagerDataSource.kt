package su.afk.kemonos.download.domain.repository

import su.afk.kemonos.download.domain.model.DownloadManagerSnapshot

internal interface DownloadManagerDataSource {
    suspend fun querySnapshots(ids: List<Long>): Map<Long, DownloadManagerSnapshot>
    suspend fun remove(id: Long): Int
}
