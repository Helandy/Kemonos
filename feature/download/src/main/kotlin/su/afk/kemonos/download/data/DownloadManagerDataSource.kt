package su.afk.kemonos.download.data

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import dagger.hilt.android.qualifiers.ApplicationContext
import su.afk.kemonos.download.domain.model.DownloadManagerSnapshot
import su.afk.kemonos.download.domain.repository.DownloadManagerDataSource
import su.afk.kemonos.utils.withIo
import javax.inject.Inject

internal class DownloadManagerDataSourceImpl @Inject constructor(
    @ApplicationContext context: Context,
) : DownloadManagerDataSource {
    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    override suspend fun querySnapshots(ids: List<Long>): Map<Long, DownloadManagerSnapshot> = withIo {
        if (ids.isEmpty()) {
            emptyMap()
        } else buildMap {
            ids.distinct().chunked(QUERY_BATCH_SIZE).forEach { batch ->
                val query = DownloadManager.Query().setFilterById(*batch.toLongArray())
                val cursor = downloadManager.query(query)
                cursor.use { c ->
                    val idIndex = c.getColumnIndex(DownloadManager.COLUMN_ID)
                    val titleIndex = c.getColumnIndex(DownloadManager.COLUMN_TITLE)
                    val statusIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val reasonIndex = c.getColumnIndex(DownloadManager.COLUMN_REASON)
                    val downloadedIndex = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                    val totalIndex = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                    val mediaTypeIndex = c.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE)
                    val remoteUriIndex = c.getColumnIndex(DownloadManager.COLUMN_URI)
                    val localUriIndex = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                    val modifiedIndex = c.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)
                    while (c.moveToNext()) {
                        val id = c.getLongOrDefault(idIndex, default = -1L)
                        if (id < 0L) continue
                        put(
                            id,
                            DownloadManagerSnapshot(
                                title = c.getStringOrNull(titleIndex),
                                status = c.getIntOrDefault(statusIndex, default = -1),
                                reason = c.getIntOrDefault(reasonIndex, default = -1),
                                bytesDownloaded = c.getLongOrDefault(downloadedIndex, default = -1L),
                                totalBytes = c.getLongOrDefault(totalIndex, default = -1L),
                                mediaType = c.getStringOrNull(mediaTypeIndex),
                                remoteUri = c.getStringOrNull(remoteUriIndex),
                                localUri = c.getStringOrNull(localUriIndex),
                                lastModifiedMs = c.getLongOrNull(modifiedIndex),
                            )
                        )
                    }
                }
            }
        }
    }

    override suspend fun remove(id: Long): Int = withIo {
        downloadManager.remove(id)
    }
}

private fun Cursor.getStringOrNull(index: Int): String? {
    if (index < 0 || isNull(index)) return null
    return getString(index)
}

private fun Cursor.getLongOrDefault(index: Int, default: Long): Long {
    if (index < 0 || isNull(index)) return default
    return getLong(index)
}

private fun Cursor.getLongOrNull(index: Int): Long? {
    if (index < 0 || isNull(index)) return null
    return getLong(index)
}

private fun Cursor.getIntOrDefault(index: Int, default: Int): Int {
    if (index < 0 || isNull(index)) return default
    return getInt(index)
}

private const val QUERY_BATCH_SIZE = 300
