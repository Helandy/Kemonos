package su.afk.kemonos.profile.presenter.setting.view.uiSetting.debug

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import su.afk.kemonos.common.imageLoader.LocalAppImageLoader
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.common.SectionSpacer
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.common.SettingsSectionTitle
import java.io.File
import kotlin.math.roundToInt

@Composable
internal fun DebugStorageInfoSection(
    enabled: Boolean,
    imageLoader: ImageLoader = LocalAppImageLoader.current,
) {
    if (!enabled) return

    val context = LocalContext.current

    var info by remember { mutableStateOf<StorageInfo?>(null) }

    LaunchedEffect(imageLoader) {
        info = withContext(Dispatchers.IO) { collectStorageInfo(context) }
    }

    SectionSpacer()
    SettingsSectionTitle(text = stringResource(R.string.settings_debug_storage_title))
    Spacer(Modifier.height(6.dp))

    val model = info
    if (model == null) {
        Text(
            text = stringResource(R.string.settings_debug_storage_loading),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        return
    }

    // Общие системные
    DebugPathBlock(
        title = stringResource(R.string.settings_debug_storage_cache_dir),
        path = model.cacheDirPath,
        sizeMb = model.cacheDirMb
    )
    DebugPathBlock(
        title = stringResource(R.string.settings_debug_storage_files_dir),
        path = model.filesDirPath,
        sizeMb = model.filesDirMb
    )

    model.externalCacheDirPath?.let {
        DebugPathBlock(
            title = stringResource(R.string.settings_debug_storage_ext_cache_dir),
            path = it,
            sizeMb = model.externalCacheDirMb ?: 0
        )
    }
    model.externalFilesDirPath?.let {
        DebugPathBlock(
            title = stringResource(R.string.settings_debug_storage_ext_files_dir),
            path = it,
            sizeMb = model.externalFilesDirMb ?: 0
        )
    }

    Spacer(Modifier.height(8.dp))

    // ТОП папок в cacheDir
    Text(
        text = "Top cache folders (top 5):",
        style = MaterialTheme.typography.bodyLarge,
    )
    Spacer(Modifier.height(6.dp))

    model.topCacheFolders.forEach { item ->
        DebugPathBlock(
            title = item.name,
            path = item.path,
            sizeMb = item.sizeMb,
        )
    }
}

@Composable
private fun DebugPathBlock(
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

private data class StorageInfo(
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

private data class CacheFolderInfo(
    val name: String,
    val path: String,
    val sizeMb: Int,
)

private fun collectStorageInfo(context: Context): StorageInfo {
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

private fun listTopCacheFolders(cacheDir: File, top: Int): List<CacheFolderInfo> {
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

private fun Long.toMbRound(): Int =
    (this / (1024.0 * 1024.0)).roundToInt()

private fun File.sizeMbSafe(): Int {
    val bytes = runCatching { dirSizeBytes(this) }.getOrDefault(0L)
    return bytes.toMbRound()
}

private fun dirSizeBytes(file: File): Long {
    if (!file.exists()) return 0L
    if (file.isFile) return file.length()

    val children = file.listFiles() ?: return 0L
    var sum = 0L
    for (child in children) sum += dirSizeBytes(child)
    return sum
}