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
import okio.Path
import su.afk.kemonos.common.constants.Constant.COIL_DISK_DIR_NAME
import su.afk.kemonos.common.constants.Constant.VIDEO_FRAMES_DIR_NAME
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
        info = withContext(Dispatchers.IO) { collectStorageInfo(context, imageLoader) }
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
    DebugPathBlock(stringResource(R.string.settings_debug_storage_cache_dir), model.cacheDirPath, model.cacheDirMb)
    DebugPathBlock(stringResource(R.string.settings_debug_storage_files_dir), model.filesDirPath, model.filesDirMb)

    model.externalCacheDirPath?.let {
        DebugPathBlock(
            stringResource(R.string.settings_debug_storage_ext_cache_dir),
            it,
            model.externalCacheDirMb ?: 0
        )
    }
    model.externalFilesDirPath?.let {
        DebugPathBlock(
            stringResource(R.string.settings_debug_storage_ext_files_dir),
            it,
            model.externalFilesDirMb ?: 0
        )
    }

    Spacer(Modifier.height(8.dp))

    // Coil
    DebugPathBlock(
        title = stringResource(R.string.settings_debug_storage_coil_custom),
        path = model.coilCustomDirPath,
        sizeMb = model.coilCustomDirMb,
    )

    model.coilActualDirPath?.let { path ->
        DebugPathBlock(
            title = stringResource(R.string.settings_debug_storage_coil_actual),
            path = path,
            sizeMb = model.coilActualDirMb ?: 0,
        )
    }

    model.coilDefaultDirPath?.let { path ->
        DebugPathBlock(
            title = stringResource(R.string.settings_debug_storage_coil_default),
            path = path,
            sizeMb = model.coilDefaultDirMb ?: 0,
        )
    }

    Spacer(Modifier.height(8.dp))

    // Video frames
    DebugPathBlock(
        title = stringResource(R.string.settings_debug_storage_video_frames),
        path = model.videoFramesDirPath,
        sizeMb = model.videoFramesDirMb,
    )
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

    // Coil
    val coilCustomDirPath: String,
    val coilCustomDirMb: Int,
    val coilActualDirPath: String?,
    val coilActualDirMb: Int?,
    val coilDefaultDirPath: String?,
    val coilDefaultDirMb: Int?,

    // Video
    val videoFramesDirPath: String,
    val videoFramesDirMb: Int,
)

private fun collectStorageInfo(context: Context, imageLoader: ImageLoader): StorageInfo {
    val cacheDir = context.cacheDir
    val filesDir = context.filesDir
    val extCache = context.externalCacheDir
    val extFiles = context.getExternalFilesDir(null)

    // Ожидаемый путь
    val coilCustomDir = cacheDir.resolve(COIL_DISK_DIR_NAME)

    // Реальный путь из DI ImageLoader (okio.Path -> File)
    val coilActualDirFile: File? = imageLoader.diskCache?.directory.toFileOrNull()

    // “Дефолтный” ImageLoader (okio.Path -> File)
    val coilDefaultDirFile: File = cacheDir.resolve("coil3_disk_cache")

    // Video frame cache dir
    val videoFramesDir = cacheDir.resolve(VIDEO_FRAMES_DIR_NAME)

    return StorageInfo(
        cacheDirPath = cacheDir.absolutePath,
        cacheDirMb = cacheDir.sizeMbSafe(),
        filesDirPath = filesDir.absolutePath,
        filesDirMb = filesDir.sizeMbSafe(),
        externalCacheDirPath = extCache?.absolutePath,
        externalCacheDirMb = extCache?.sizeMbSafe(),
        externalFilesDirPath = extFiles?.absolutePath,
        externalFilesDirMb = extFiles?.sizeMbSafe(),

        coilCustomDirPath = coilCustomDir.absolutePath,
        coilCustomDirMb = coilCustomDir.sizeMbSafe(),

        coilActualDirPath = coilActualDirFile?.absolutePath,
        coilActualDirMb = coilActualDirFile?.sizeMbSafe(),

        coilDefaultDirPath = coilDefaultDirFile.absolutePath,
        coilDefaultDirMb = coilDefaultDirFile.sizeMbSafe(),

        videoFramesDirPath = videoFramesDir.absolutePath,
        videoFramesDirMb = videoFramesDir.sizeMbSafe(),
    )
}


private fun Path?.toFileOrNull(): File? = this?.toFile()

private fun File.sizeMbSafe(): Int {
    val bytes = runCatching { dirSizeBytes(this) }.getOrDefault(0L)
    return (bytes / (1024.0 * 1024.0)).roundToInt()
}

private fun dirSizeBytes(file: File): Long {
    if (!file.exists()) return 0L
    if (file.isFile) return file.length()

    val children = file.listFiles() ?: return 0L
    var sum = 0L
    for (child in children) sum += dirSizeBytes(child)
    return sum
}