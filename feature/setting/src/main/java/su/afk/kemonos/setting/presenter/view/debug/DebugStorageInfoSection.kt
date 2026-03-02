package su.afk.kemonos.setting.presenter.view.debug

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import java.io.File
import kotlin.math.roundToInt

@Composable
internal fun DebugPathBlock(
    title: String,
    path: String,
    sizeMb: Int,
) {
    Column {
        Text(
            text = "$title: ${sizeMb}MB",
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = path,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily.Monospace,
        )
        Spacer(Modifier.height(8.dp))
    }
}

internal data class StorageInfo(
    val cacheDirPath: String,
    val cacheDirMb: Int,
    val filesDirPath: String,
    val filesDirMb: Int,
    val externalCacheDirPath: String?,
    val externalCacheDirMb: Int?,
    val externalFilesDirPath: String?,
    val externalFilesDirMb: Int?,

    val topCacheFolders: List<CacheFolderInfo>,
)

internal data class CacheFolderInfo(
    val name: String,
    val path: String,
    val sizeMb: Int,
)

internal fun collectStorageInfo(context: Context): StorageInfo {
    val cacheDir = context.cacheDir
    val filesDir = context.filesDir
    val extCache = context.externalCacheDir
    val extFiles = context.getExternalFilesDir(null)

    val top5 = listTopCacheFolders(cacheDir, top = 5)

    return StorageInfo(
        cacheDirPath = cacheDir.absolutePath,
        cacheDirMb = cacheDir.sizeMbSafe(),
        filesDirPath = filesDir.absolutePath,
        filesDirMb = filesDir.sizeMbSafe(),
        externalCacheDirPath = extCache?.absolutePath,
        externalCacheDirMb = extCache?.sizeMbSafe(),
        externalFilesDirPath = extFiles?.absolutePath,
        externalFilesDirMb = extFiles?.sizeMbSafe(),
        topCacheFolders = top5,
    )
}

internal fun listTopCacheFolders(cacheDir: File, top: Int): List<CacheFolderInfo> {
    val children = cacheDir.listFiles().orEmpty()

    return children
        .filter { it.exists() }
        .map { f ->
            val bytes = runCatching { dirSizeBytes(f) }.getOrDefault(0L)
            CacheFolderInfo(
                name = f.name,
                path = f.absolutePath,
                sizeMb = bytes.toMbRound(),
            )
        }
        .sortedByDescending { it.sizeMb }
        .take(top)
}

internal fun Long.toMbRound(): Int =
    (this / (1024.0 * 1024.0)).roundToInt()

internal fun File.sizeMbSafe(): Int {
    val bytes = runCatching { dirSizeBytes(this) }.getOrDefault(0L)
    return bytes.toMbRound()
}

internal fun dirSizeBytes(file: File): Long {
    if (!file.exists()) return 0L
    if (file.isFile) return file.length()

    val children = file.listFiles() ?: return 0L
    var sum = 0L
    for (child in children) sum += dirSizeBytes(child)
    return sum
}