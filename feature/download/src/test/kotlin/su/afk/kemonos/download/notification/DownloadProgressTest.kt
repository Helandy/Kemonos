package su.afk.kemonos.download.notification

import android.app.DownloadManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class DownloadProgressTest {

    @Test
    fun `counts finished against everything in the batch`() {
        val progress = DownloadProgress.from(
            listOf(
                DownloadManager.STATUS_SUCCESSFUL,
                DownloadManager.STATUS_SUCCESSFUL,
                DownloadManager.STATUS_RUNNING,
                DownloadManager.STATUS_PENDING,
            )
        )

        assertEquals(2, progress.completed)
        assertEquals(2, progress.active)
        assertEquals(4, progress.total)
        assertFalse(progress.isFinished)
    }

    @Test
    fun `a failure still counts towards the total so the bar can finish`() {
        val progress = DownloadProgress.from(
            listOf(
                DownloadManager.STATUS_SUCCESSFUL,
                DownloadManager.STATUS_FAILED,
            )
        )

        assertEquals(1, progress.completed)
        assertEquals(1, progress.failed)
        assertEquals(2, progress.total)
        /** Ничего не качается — прогресс держать незачем. */
        assertTrue(progress.isFinished)
    }

    @Test
    fun `statuses outside the queue are ignored`() {
        /** Убранные из DownloadManager записи не должны раздувать знаменатель. */
        val progress = DownloadProgress.from(
            listOf(
                DownloadManager.STATUS_RUNNING,
                -1,
                999,
            )
        )

        assertEquals(1, progress.total)
        assertEquals(1, progress.active)
    }

    @Test
    fun `nothing tracked means nothing to show`() {
        val progress = DownloadProgress.from(emptyList())

        assertFalse(progress.hasAnything)
        assertTrue(progress.isFinished)
    }
}
