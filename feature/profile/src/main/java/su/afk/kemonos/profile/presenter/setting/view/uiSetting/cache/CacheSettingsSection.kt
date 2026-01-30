package su.afk.kemonos.profile.presenter.setting.view.uiSetting.cache

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.CacheSizeSliderRow
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.common.SectionSpacer
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.common.SettingsSectionTitle

@Composable
internal fun CacheSettingsSection(
    coilCacheSizeMb: Int,
    previewVideoSizeMb: Int,
    onCoilCache: (Int) -> Unit,
    onPreviewCache: (Int) -> Unit,
) {
    SectionSpacer()
    SettingsSectionTitle(text = stringResource(R.string.settings_cache_sizes_title))
    Spacer(Modifier.height(6.dp))

    CacheSizeSliderRow(
        title = stringResource(R.string.settings_cache_images_size_title),
        currentMb = coilCacheSizeMb,
        onChangeMb = onCoilCache,
    )

    Spacer(Modifier.height(10.dp))

    CacheSizeSliderRow(
        title = stringResource(R.string.settings_cache_video_previews_size_title),
        currentMb = previewVideoSizeMb,
        onChangeMb = onPreviewCache,
    )
}
