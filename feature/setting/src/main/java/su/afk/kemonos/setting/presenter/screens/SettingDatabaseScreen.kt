package su.afk.kemonos.setting.presenter.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.setting.R
import su.afk.kemonos.setting.presenter.SettingState.Event
import su.afk.kemonos.setting.presenter.SettingState.State
import su.afk.kemonos.setting.presenter.view.CacheSizeSliderRow
import su.afk.kemonos.setting.presenter.view.cache.CacheRow
import su.afk.kemonos.setting.presenter.view.common.SectionSpacer
import su.afk.kemonos.setting.presenter.view.common.SettingsSectionTitle
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingDatabaseScreen(
    state: State,
    onEvent: (Event) -> Unit,
) {
    SettingsScreenScaffold(
        title = stringResource(R.string.settings_hub_database_title),
        onBack = { onEvent(Event.Back) },
        isLoading = state.loading,
        contentModifier = Modifier.padding(horizontal = 8.dp),
        topBarScroll = TopBarScroll.Pinned,
    ) {
        SectionSpacer()
        SettingsSectionTitle(text = stringResource(R.string.settings_cache_sizes_title))
        Spacer(Modifier.height(6.dp))

        CacheSizeSliderRow(
            title = stringResource(R.string.settings_cache_images_size_title),
            currentMb = state.uiSettingModel.coilCacheSizeMb,
            onChangeMb = { onEvent(Event.ChangeViewSetting.CoilCacheSizeMb(it)) },
        )

        Spacer(Modifier.height(10.dp))

        CacheSizeSliderRow(
            title = stringResource(R.string.settings_cache_video_previews_size_title),
            currentMb = state.uiSettingModel.previewVideoSizeMb,
            onChangeMb = { onEvent(Event.ChangeViewSetting.PreviewVideoSizeMb(it)) },
        )

        Spacer(Modifier.height(18.dp))

        Column {
            Text(
                text = stringResource(R.string.settings_cache_title),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.settings_cache_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(14.dp))

            CacheRow(
                title = stringResource(R.string.settings_cache_tags_kemono),
                time = state.tagsKemonoCache,
                dateFormatMode = state.uiSettingModel.dateFormatMode,
                onClear = { onEvent(Event.CacheClearAction.Tags(SelectedSite.K)) },
                busy = state.clearInProgress
            )
            CacheRow(
                title = stringResource(R.string.settings_cache_tags_coomer),
                time = state.tagsCoomerCache,
                dateFormatMode = state.uiSettingModel.dateFormatMode,
                onClear = { onEvent(Event.CacheClearAction.Tags(SelectedSite.C)) },
                busy = state.clearInProgress
            )

            CacheRow(
                title = stringResource(R.string.settings_cache_creators_kemono),
                time = state.creatorsKemonoCache,
                dateFormatMode = state.uiSettingModel.dateFormatMode,
                onClear = { onEvent(Event.CacheClearAction.Creators(SelectedSite.K)) },
                busy = state.clearInProgress
            )
            CacheRow(
                title = stringResource(R.string.settings_cache_creators_coomer),
                time = state.creatorsCoomerCache,
                dateFormatMode = state.uiSettingModel.dateFormatMode,
                onClear = { onEvent(Event.CacheClearAction.Creators(SelectedSite.C)) },
                busy = state.clearInProgress
            )

            CacheRow(
                title = stringResource(R.string.settings_cache_profiles),
                time = state.creatorProfilesCache,
                dateFormatMode = state.uiSettingModel.dateFormatMode,
                onClear = { onEvent(Event.CacheClearAction.CreatorProfiles) },
                busy = state.clearInProgress
            )

            CacheRow(
                title = stringResource(R.string.settings_cache_creator_posts_pages),
                time = state.creatorPostsCache,
                dateFormatMode = state.uiSettingModel.dateFormatMode,
                onClear = { onEvent(Event.CacheClearAction.CreatorPostsPages) },
                busy = state.clearInProgress
            )

            CacheRow(
                title = stringResource(R.string.settings_cache_post_contents),
                time = state.postContentsCache,
                dateFormatMode = state.uiSettingModel.dateFormatMode,
                onClear = { onEvent(Event.CacheClearAction.PostContents) },
                busy = state.clearInProgress
            )

            CacheRow(
                title = stringResource(R.string.settings_cache_popular_kemono),
                time = state.popularKemonoCache,
                dateFormatMode = state.uiSettingModel.dateFormatMode,
                onClear = { onEvent(Event.CacheClearAction.PopularPosts) },
                busy = state.clearInProgress
            )

            CacheRow(
                title = stringResource(R.string.settings_cache_fav_posts_kemono),
                time = state.favPostsKemonoCache,
                dateFormatMode = state.uiSettingModel.dateFormatMode,
                onClear = { onEvent(Event.CacheClearAction.FavoritesPosts) },
                busy = state.clearInProgress
            )

            CacheRow(
                title = stringResource(R.string.settings_cache_fav_authors_kemono),
                time = state.favCreatorsKemonoCache,
                dateFormatMode = state.uiSettingModel.dateFormatMode,
                onClear = { onEvent(Event.CacheClearAction.FavoritesArtists) },
                busy = state.clearInProgress,
                showDivider = false,
            )
        }
    }
}

@Preview(name = "Setting Database", showBackground = true)
@Composable
private fun PreviewSettingDatabaseScreen() {
    SettingsPreview {
        SettingDatabaseScreen(
            state = previewSettingState(),
            onEvent = {},
        )
    }
}
