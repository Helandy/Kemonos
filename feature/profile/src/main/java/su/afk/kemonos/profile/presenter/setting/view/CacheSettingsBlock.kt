package su.afk.kemonos.profile.presenter.setting.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.presenter.setting.CacheClearAction
import su.afk.kemonos.profile.presenter.setting.SettingState

@Composable
internal fun CacheSettingsBlock(
    state: SettingState.State,
    formatDateTime: (Long) -> String,
    onClear: (CacheClearAction) -> Unit,
) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
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
                formatDateTime = formatDateTime,
                onClear = { onClear(CacheClearAction.Tags(SelectedSite.K)) },
                busy = state.clearInProgress
            )
            CacheRow(
                title = stringResource(R.string.settings_cache_tags_coomer),
                time = state.tagsCoomerCache,
                formatDateTime = formatDateTime,
                onClear = { onClear(CacheClearAction.Tags(SelectedSite.C)) },
                busy = state.clearInProgress
            )

            CacheRow(
                title = stringResource(R.string.settings_cache_creators_kemono),
                time = state.creatorsKemonoCache,
                formatDateTime = formatDateTime,
                onClear = { onClear(CacheClearAction.Creators(SelectedSite.K)) },
                busy = state.clearInProgress
            )
            CacheRow(
                title = stringResource(R.string.settings_cache_creators_coomer),
                time = state.creatorsCoomerCache,
                formatDateTime = formatDateTime,
                onClear = { onClear(CacheClearAction.Creators(SelectedSite.C)) },
                busy = state.clearInProgress
            )

            CacheRow(
                title = stringResource(R.string.settings_cache_profiles),
                time = state.creatorProfilesCache,
                formatDateTime = formatDateTime,
                onClear = { onClear(CacheClearAction.CreatorProfiles) },
                busy = state.clearInProgress
            )

            CacheRow(
                title = stringResource(R.string.settings_cache_creator_posts_pages),
                time = state.creatorPostsCache,
                formatDateTime = formatDateTime,
                onClear = { onClear(CacheClearAction.CreatorPostsPages) },
                busy = state.clearInProgress
            )

            CacheRow(
                title = stringResource(R.string.settings_cache_post_contents),
                time = state.postContentsCache,
                formatDateTime = formatDateTime,
                onClear = { onClear(CacheClearAction.PostContents) },
                busy = state.clearInProgress
            )

            CacheRow(
                title = stringResource(R.string.settings_cache_popular_kemono),
                time = state.popularKemonoCache,
                formatDateTime = formatDateTime,
                onClear = { onClear(CacheClearAction.PopularPosts) },
                busy = state.clearInProgress
            )

            CacheRow(
                title = stringResource(R.string.settings_cache_fav_posts_kemono),
                time = state.favPostsKemonoCache,
                formatDateTime = formatDateTime,
                onClear = { onClear(CacheClearAction.FavoritesPosts) },
                busy = state.clearInProgress
            )

            CacheRow(
                title = stringResource(R.string.settings_cache_fav_authors_kemono),
                time = state.favCreatorsKemonoCache,
                formatDateTime = formatDateTime,
                onClear = { onClear(CacheClearAction.FavoritesArtists) },
                busy = state.clearInProgress,
                showDivider = false,
            )
        }
    }
}

