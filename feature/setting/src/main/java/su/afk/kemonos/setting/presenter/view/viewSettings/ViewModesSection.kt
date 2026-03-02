package su.afk.kemonos.setting.presenter.view.viewSettings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.PostsSize
import su.afk.kemonos.preferences.ui.PostsViewMode
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.setting.R
import su.afk.kemonos.setting.presenter.SettingState.Event
import su.afk.kemonos.setting.presenter.view.CreatorsViewModeRow
import su.afk.kemonos.setting.presenter.view.PostsViewModeRow
import su.afk.kemonos.setting.presenter.view.postsSize.PostsSizeRow

@Composable
internal fun ViewModesSection(
    ui: UiSettingModel,
    onEvent: (Event) -> Unit,
) {
    Spacer(Modifier.height(6.dp))

    CreatorsViewModeRow(
        title = stringResource(R.string.settings_ui_creators_view_mode),
        value = ui.creatorsViewMode,
        onChange = { onEvent(Event.ChangeViewSetting.CreatorsViewMode(it)) },
    )

    CreatorsViewModeRow(
        title = stringResource(R.string.settings_ui_creators_favorite_view_mode),
        value = ui.creatorsFavoriteViewMode,
        onChange = { onEvent(Event.ChangeViewSetting.CreatorsFavoriteViewMode(it)) },
    )

    Spacer(Modifier.height(8.dp))

    listOf(
        PostsModeConfig(
            modeTitleRes = R.string.settings_ui_posts_view_mode_profile,
            sizeTitleRes = R.string.settings_posts_size_profile_title,
            mode = ui.profilePostsViewMode,
            size = ui.profilePostsGridSize,
            onModeChange = { Event.ChangeViewSetting.ProfilePostsViewMode(it) },
            onSizeChange = { Event.ChangeViewSetting.ProfilePostsGridSize(it) },
        ),
        PostsModeConfig(
            modeTitleRes = R.string.settings_ui_posts_view_mode_favorite,
            sizeTitleRes = R.string.settings_posts_size_favorite_title,
            mode = ui.favoritePostsViewMode,
            size = ui.favoritePostsGridSize,
            onModeChange = { Event.ChangeViewSetting.FavoritePostsViewMode(it) },
            onSizeChange = { Event.ChangeViewSetting.FavoritePostsGridSize(it) },
        ),
        PostsModeConfig(
            modeTitleRes = R.string.settings_ui_posts_view_mode_popular,
            sizeTitleRes = R.string.settings_posts_size_popular_title,
            mode = ui.popularPostsViewMode,
            size = ui.popularPostsGridSize,
            onModeChange = { Event.ChangeViewSetting.PopularPostsViewMode(it) },
            onSizeChange = { Event.ChangeViewSetting.PopularPostsGridSize(it) },
        ),
        PostsModeConfig(
            modeTitleRes = R.string.settings_ui_posts_view_mode_tags,
            sizeTitleRes = R.string.settings_posts_size_tags_title,
            mode = ui.tagsPostsViewMode,
            size = ui.tagsPostsGridSize,
            onModeChange = { Event.ChangeViewSetting.TagsPostsViewMode(it) },
            onSizeChange = { Event.ChangeViewSetting.TagsPostsGridSize(it) },
        ),
        PostsModeConfig(
            modeTitleRes = R.string.settings_ui_posts_view_mode_search,
            sizeTitleRes = R.string.settings_posts_size_search_title,
            mode = ui.searchPostsViewMode,
            size = ui.searchPostsGridSize,
            onModeChange = { Event.ChangeViewSetting.SearchPostsViewMode(it) },
            onSizeChange = { Event.ChangeViewSetting.SearchPostsGridSize(it) },
        ),
    ).forEach { config ->
        PostsViewModeRow(
            title = stringResource(config.modeTitleRes),
            value = config.mode,
            onChange = { onEvent(config.onModeChange(it)) },
        )
        PostsSizeRow(
            title = stringResource(config.sizeTitleRes),
            value = config.size,
            onChange = { onEvent(config.onSizeChange(it)) },
        )
    }
}

private data class PostsModeConfig(
    @param:StringRes val modeTitleRes: Int,
    @param:StringRes val sizeTitleRes: Int,
    val mode: PostsViewMode,
    val size: PostsSize,
    val onModeChange: (PostsViewMode) -> Event,
    val onSizeChange: (PostsSize) -> Event,
)
