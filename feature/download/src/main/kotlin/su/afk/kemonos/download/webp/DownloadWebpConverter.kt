package su.afk.kemonos.download.webp

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import su.afk.kemonos.utils.withIo
import java.io.File
import javax.inject.Inject

internal sealed interface WebpConversionResult {
    data class Converted(val fileName: String, val savedBytes: Long) : WebpConversionResult
    data object Skipped : WebpConversionResult
    data class Failed(val cause: Throwable) : WebpConversionResult
}

/**
 * Пережимает скачанный файл в webp.
 *
 * Порядок операций выбран так, чтобы исходник нельзя было потерять: новый файл
 * сначала целиком собирается в кеше приложения, потом публикуется рядом, и лишь
 * когда он на месте и не пустой — удаляется старый. Любой сбой на любом шаге
 * оставляет исходный файл нетронутым.
 */
internal class DownloadWebpConverter @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    suspend fun convert(
        localUri: String,
        fileName: String,
        quality: Int,
    ): WebpConversionResult = withIo {
        if (!WebpConversionRules.shouldConvert(fileName)) return@withIo WebpConversionResult.Skipped

        runCatching { convertBlocking(localUri, fileName, quality) }
            .getOrElse { WebpConversionResult.Failed(it) }
    }

    private fun convertBlocking(
        localUri: String,
        fileName: String,
        quality: Int,
    ): WebpConversionResult {
        val sourceUri = localUri.toUri()
        val originalSize = sourceUri.sizeBytes()

        val bitmap = context.contentResolver.openInputStream(sourceUri)?.use { input ->
            BitmapFactory.decodeStream(input)
        } ?: return WebpConversionResult.Skipped

        val encoded = try {
            encodeToCache(bitmap, fileName, quality)
        } finally {
            bitmap.recycle()
        }

        /** Пережали в файл больше исходного — смысла в замене нет. */
        if (originalSize in 1..encoded.length()) {
            encoded.delete()
            return WebpConversionResult.Skipped
        }

        val targetName = WebpConversionRules.targetFileName(fileName)
        val published = publish(encoded, targetName, sourceUri)
        encoded.delete()

        if (published == null) return WebpConversionResult.Skipped

        /** Публикация подтверждена — только теперь исходник лишний. */
        runCatching { context.contentResolver.delete(sourceUri, null, null) }

        return WebpConversionResult.Converted(
            fileName = targetName,
            savedBytes = (originalSize - published).coerceAtLeast(0L),
        )
    }

    private fun encodeToCache(bitmap: Bitmap, fileName: String, quality: Int): File {
        val target = File(context.cacheDir, "webp-${System.nanoTime()}-$fileName.webp")
        val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            @Suppress("DEPRECATION")
            Bitmap.CompressFormat.WEBP
        }

        target.outputStream().use { output ->
            bitmap.compress(format, quality, output)
        }
        return target
    }

    /**
     * Кладёт готовый webp в ту же папку загрузок.
     *
     * @return размер опубликованного файла, либо null если положить не удалось —
     * тогда исходник остаётся на месте.
     */
    private fun publish(encoded: File, targetName: String, sourceUri: Uri): Long? {
        val relativePath = sourceUri.relativePath() ?: return null

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, targetName)
            put(MediaStore.MediaColumns.MIME_TYPE, WEBP_MIME)
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
        }

        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val target = context.contentResolver.insert(collection, values) ?: return null

        val written = runCatching {
            context.contentResolver.openOutputStream(target)?.use { output ->
                encoded.inputStream().use { input -> input.copyTo(output) }
            } ?: return@runCatching 0L
            target.sizeBytes()
        }.getOrDefault(0L)

        if (written <= 0L) {
            /** Пустая запись хуже, чем её отсутствие: убираем за собой. */
            runCatching { context.contentResolver.delete(target, null, null) }
            return null
        }

        return written
    }

    private fun Uri.sizeBytes(): Long = runCatching {
        context.contentResolver.openFileDescriptor(this, "r")?.use { it.statSize }
    }.getOrNull() ?: 0L

    private fun Uri.relativePath(): String? = runCatching {
        context.contentResolver.query(
            this,
            arrayOf(MediaStore.MediaColumns.RELATIVE_PATH),
            null,
            null,
            null,
        )?.use { cursor ->
            if (!cursor.moveToFirst()) return@use null
            cursor.getString(0)?.takeIf { it.isNotBlank() }
        }
    }.getOrNull()

    private companion object {
        const val WEBP_MIME = "image/webp"
    }
}
