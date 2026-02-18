package su.afk.kemonos.download.data

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal interface DownloadManagerDataSource {
    fun querySnapshots(ids: List<Long>): Map<Long, DownloadManagerSnapshot>
    fun remove(id: Long): Int
}

internal data class DownloadManagerSnapshot(
    val title: String?,
    val status: Int,
    val reason: Int,
    val bytesDownloaded: Long,
    val totalBytes: Long,
    val mediaType: String?,
    val remoteUri: String?,
    val localUri: String?,
    val lastModifiedMs: Long?,
)

internal class DownloadManagerDataSourceImpl @Inject constructor(
    @ApplicationContext context: Context,
) : DownloadManagerDataSource {
    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    override fun querySnapshots(ids: List<Long>): Map<Long, DownloadManagerSnapshot> {
        if (ids.isEmpty()) return emptyMap()
        return buildMap {
            ids.distinct().chunked(QUERY_BATCH_SIZE).forEach { batch ->
                val query = DownloadManager.Query().setFilterById(*batch.toLongArray())
                val cursor = downloadManager.query(query)
                cursor.use { c ->
                    while (c.moveToNext()) {
                        val id = c.getLongByName(DownloadManager.COLUMN_ID)
                        put(
                            id,
                            DownloadManagerSnapshot(
                                title = c.getStringByName(DownloadManager.COLUMN_TITLE),
                                status = c.getIntByName(DownloadManager.COLUMN_STATUS),
                                reason = c.getIntByName(DownloadManager.COLUMN_REASON),
                                bytesDownloaded = c.getLongByName(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR),
                                totalBytes = c.getLongByName(DownloadManager.COLUMN_TOTAL_SIZE_BYTES),
                                mediaType = c.getStringByName(DownloadManager.COLUMN_MEDIA_TYPE),
                                remoteUri = c.getStringByName(DownloadManager.COLUMN_URI),
                                localUri = c.getStringByName(DownloadManager.COLUMN_LOCAL_URI),
                                lastModifiedMs = c.getLongByName(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP),
                            )
                        )
                    }
                }
            }
        }
    }

    override fun remove(id: Long): Int = downloadManager.remove(id)
}

private fun Cursor.getStringByName(name: String): String? {
    val index = getColumnIndex(name)
    if (index < 0 || isNull(index)) return null
    return getString(index)
}

private fun Cursor.getLongByName(name: String): Long {
    val index = getColumnIndex(name)
    if (index < 0 || isNull(index)) return -1L
    return getLong(index)
}

private fun Cursor.getIntByName(name: String): Int {
    val index = getColumnIndex(name)
    if (index < 0 || isNull(index)) return -1
    return getInt(index)
}

private const val QUERY_BATCH_SIZE = 300
