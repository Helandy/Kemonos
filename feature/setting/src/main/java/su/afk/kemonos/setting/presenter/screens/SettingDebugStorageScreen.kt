package su.afk.kemonos.setting.presenter.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import su.afk.kemonos.setting.R
import su.afk.kemonos.setting.presenter.SettingState.Event
import su.afk.kemonos.setting.presenter.SettingState.State
import su.afk.kemonos.setting.presenter.view.common.SectionSpacer
import su.afk.kemonos.setting.presenter.view.common.SettingsSectionTitle
import su.afk.kemonos.setting.presenter.view.debug.DebugPathBlock
import su.afk.kemonos.setting.presenter.view.debug.StorageInfo
import su.afk.kemonos.setting.presenter.view.debug.collectStorageInfo
import su.afk.kemonos.ui.imageLoader.LocalAppImageLoader
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.CenterBackTopBar
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingDebugStorageScreen(
    state: State,
    onEvent: (Event) -> Unit,
) {
    val imageLoader = LocalAppImageLoader.current

    BaseScreen(
        contentModifier = Modifier.padding(horizontal = 8.dp),
        isScroll = true,
        isLoading = state.loading,
        topBarScroll = TopBarScroll.Pinned,
        customTopBar = { scrollBehavior ->
            CenterBackTopBar(
                title = stringResource(R.string.settings_debug_storage_title),
                onBack = { onEvent(Event.Back) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) {
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
        } else {
            DebugPathBlock(
                title = stringResource(R.string.settings_debug_storage_cache_dir),
                path = model.cacheDirPath,
                sizeMb = model.cacheDirMb,
            )
            DebugPathBlock(
                title = stringResource(R.string.settings_debug_storage_files_dir),
                path = model.filesDirPath,
                sizeMb = model.filesDirMb,
            )

            model.externalCacheDirPath?.let {
                DebugPathBlock(
                    title = stringResource(R.string.settings_debug_storage_ext_cache_dir),
                    path = it,
                    sizeMb = model.externalCacheDirMb ?: 0,
                )
            }
            model.externalFilesDirPath?.let {
                DebugPathBlock(
                    title = stringResource(R.string.settings_debug_storage_ext_files_dir),
                    path = it,
                    sizeMb = model.externalFilesDirMb ?: 0,
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.settings_debug_storage_top_cache_folders_title),
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
    }
}
