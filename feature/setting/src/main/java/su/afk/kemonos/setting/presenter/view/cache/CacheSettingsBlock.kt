package su.afk.kemonos.setting.presenter.view.cache

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.profile.R
import su.afk.kemonos.setting.presenter.SettingState
import su.afk.kemonos.setting.presenter.SettingState.State
import su.afk.kemonos.ui.preview.KemonosPreviewScreen

@Composable
internal fun CacheSettingsBlock(
    state: State,
    dateFormatMode: DateFormatMode,
    onEvent: (SettingState.Event) -> Unit
) {
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
            dateFormatMode = dateFormatMode,
            onClear = { onEvent(SettingState.Event.CacheClearAction.Tags(SelectedSite.K)) },
            busy = state.clearInProgress
        )
        CacheRow(
            title = stringResource(R.string.settings_cache_tags_coomer),
            time = state.tagsCoomerCache,
            dateFormatMode = dateFormatMode,
            onClear = { onEvent(SettingState.Event.CacheClearAction.Tags(SelectedSite.C)) },
            busy = state.clearInProgress
        )

        CacheRow(
            title = stringResource(R.string.settings_cache_creators_kemono),
            time = state.creatorsKemonoCache,
            dateFormatMode = dateFormatMode,
            onClear = { onEvent(SettingState.Event.CacheClearAction.Creators(SelectedSite.K)) },
            busy = state.clearInProgress
        )
        CacheRow(
            title = stringResource(R.string.settings_cache_creators_coomer),
            time = state.creatorsCoomerCache,
            dateFormatMode = dateFormatMode,
            onClear = { onEvent(SettingState.Event.CacheClearAction.Creators(SelectedSite.C)) },
            busy = state.clearInProgress
        )

        CacheRow(
            title = stringResource(R.string.settings_cache_profiles),
            time = state.creatorProfilesCache,
            dateFormatMode = dateFormatMode,
            onClear = { onEvent(SettingState.Event.CacheClearAction.CreatorProfiles) },
            busy = state.clearInProgress
        )

        CacheRow(
            title = stringResource(R.string.settings_cache_creator_posts_pages),
            time = state.creatorPostsCache,
            dateFormatMode = dateFormatMode,
            onClear = { onEvent(SettingState.Event.CacheClearAction.CreatorPostsPages) },
            busy = state.clearInProgress
        )

        CacheRow(
            title = stringResource(R.string.settings_cache_post_contents),
            time = state.postContentsCache,
            dateFormatMode = dateFormatMode,
            onClear = { onEvent(SettingState.Event.CacheClearAction.PostContents) },
            busy = state.clearInProgress
        )

        CacheRow(
            title = stringResource(R.string.settings_cache_popular_kemono),
            time = state.popularKemonoCache,
            dateFormatMode = dateFormatMode,
            onClear = { onEvent(SettingState.Event.CacheClearAction.PopularPosts) },
            busy = state.clearInProgress
        )

        CacheRow(
            title = stringResource(R.string.settings_cache_fav_posts_kemono),
            time = state.favPostsKemonoCache,
            dateFormatMode = dateFormatMode,
            onClear = { onEvent(SettingState.Event.CacheClearAction.FavoritesPosts) },
            busy = state.clearInProgress
        )

        CacheRow(
            title = stringResource(R.string.settings_cache_fav_authors_kemono),
            time = state.favCreatorsKemonoCache,
            dateFormatMode = dateFormatMode,
            onClear = { onEvent(SettingState.Event.CacheClearAction.FavoritesArtists) },
            busy = state.clearInProgress,
            showDivider = false,
        )
    }
}

@Preview("PreviewCacheSettingsBlock")
@Composable
private fun PreviewCacheSettingsBlock() {
    KemonosPreviewScreen {
        CacheSettingsBlock(
            state = State(),
            dateFormatMode = DateFormatMode.DD_MM_YYYY,
            onEvent = {},
        )
    }
}