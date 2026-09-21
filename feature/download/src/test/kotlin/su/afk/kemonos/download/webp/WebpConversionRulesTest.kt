package su.afk.kemonos.download.webp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class WebpConversionRulesTest {

    @Test
    fun `png and jpeg are worth converting`() {
        assertTrue(WebpConversionRules.shouldConvert("1.png"))
        assertTrue(WebpConversionRules.shouldConvert("1.PNG"))
        assertTrue(WebpConversionRules.shouldConvert("photo.jpg"))
        assertTrue(WebpConversionRules.shouldConvert("photo.jpeg"))
    }

    @Test
    fun `already compressed or animated images are left alone`() {
        /** Повторное кодирование webp — чистая потеря качества. */
        assertFalse(WebpConversionRules.shouldConvert("1.webp"))
        /** Статичный webp убил бы анимацию. */
        assertFalse(WebpConversionRules.shouldConvert("loop.gif"))
    }

    @Test
    fun `non images are left alone`() {
        assertFalse(WebpConversionRules.shouldConvert("archive.zip"))
        assertFalse(WebpConversionRules.shouldConvert("clip.mp4"))
        assertFalse(WebpConversionRules.shouldConvert("no-extension"))
        assertFalse(WebpConversionRules.shouldConvert(null))
        assertFalse(WebpConversionRules.shouldConvert(""))
    }

    @Test
    fun `target keeps the name and swaps the extension`() {
        assertEquals("1.webp", WebpConversionRules.targetFileName("1.png"))
        assertEquals("my.photo.webp", WebpConversionRules.targetFileName("my.photo.jpg"))
        assertEquals("no-extension.webp", WebpConversionRules.targetFileName("no-extension"))
    }
}
