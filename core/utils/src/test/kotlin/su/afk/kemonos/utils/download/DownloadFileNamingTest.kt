package su.afk.kemonos.utils.download

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DownloadFileNamingTest {

    @Test
    fun `sha256 name is hash-like`() {
        val name = "4f2c1b9a8e7d6c5b4a39281706f5e4d3c2b1a09876543210fedcba9876543210.png"
        assertTrue(isHashLikeFileName(name))
    }

    @Test
    fun `readable name is not hash-like`() {
        assertFalse(isHashLikeFileName("cover art final.png"))
        assertFalse(isHashLikeFileName("page01.jpg"))
        assertFalse(isHashLikeFileName("deadbeef.png"))
    }

    @Test
    fun `blank name is not hash-like`() {
        assertFalse(isHashLikeFileName(null))
        assertFalse(isHashLikeFileName(""))
    }

    @Test
    fun `extension comes from the source name`() {
        assertEquals("1.png", numberedFileName(1, "4f2c1b9a8e7d6c5b4a3928170.png"))
        assertEquals("12.webp", numberedFileName(12, "4f2c1b9a8e7d6c5b4a3928170.webp"))
    }

    @Test
    fun `extension falls back to the url`() {
        val url = "https://file.pawchive.pw/data/4f/2c/4f2c1b9a8e7d6c5b4a3928170.jpg?f=name"
        assertEquals("3.jpg", numberedFileName(3, sourceName = null, sourceUrl = url))
    }

    @Test
    fun `number without extension when there is none`() {
        assertEquals("5", numberedFileName(5, sourceName = "no-extension", sourceUrl = null))
    }
}
