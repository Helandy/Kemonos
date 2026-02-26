package su.afk.kemonos.setting.presenter.view.uiSetting.viewSettings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.CreatorViewMode
import su.afk.kemonos.preferences.ui.PostsViewMode
import su.afk.kemonos.profile.R
import su.afk.kemonos.setting.presenter.view.uiSetting.CreatorsViewModeRow
import su.afk.kemonos.setting.presenter.view.uiSetting.PostsViewModeRow

@Composable
internal fun ViewModesSection(
    creatorsViewMode: CreatorViewMode,
    creatorsFavoriteViewMode: CreatorViewMode,
    profilePostsViewMode: PostsViewMode,
    favoritePostsViewMode: PostsViewMode,
    popularPostsViewMode: PostsViewMode,
    tagsPostsViewMode: PostsViewMode,
    searchPostsViewMode: PostsViewMode,
    onCreatorsViewMode: (CreatorViewMode) -> Unit,
    onCreatorsFavoriteViewMode: (CreatorViewMode) -> Unit,
    onProfilePostsViewMode: (PostsViewMode) -> Unit,
    onFavoritePostsViewMode: (PostsViewMode) -> Unit,
    onPopularPostsViewMode: (PostsViewMode) -> Unit,
    onTagsPostsViewMode: (PostsViewMode) -> Unit,
    onSearchPostsViewMode: (PostsViewMode) -> Unit,
) {
    Spacer(Modifier.height(6.dp))

    CreatorsViewModeRow(
        title = stringResource(R.string.settings_ui_creators_view_mode),
        value = creatorsViewMode,
        onChange = onCreatorsViewMode
    )

    CreatorsViewModeRow(
        title = stringResource(R.string.settings_ui_creators_favorite_view_mode),
        value = creatorsFavoriteViewMode,
        onChange = onCreatorsFavoriteViewMode
    )

    Spacer(Modifier.height(8.dp))

    PostsViewModeRow(
        title = stringResource(R.string.settings_ui_posts_view_mode_profile),
        value = profilePostsViewMode,
        onChange = onProfilePostsViewMode
    )
    PostsViewModeRow(
        title = stringResource(R.string.settings_ui_posts_view_mode_favorite),
        value = favoritePostsViewMode,
        onChange = onFavoritePostsViewMode
    )
    PostsViewModeRow(
        title = stringResource(R.string.settings_ui_posts_view_mode_popular),
        value = popularPostsViewMode,
        onChange = onPopularPostsViewMode
    )
    PostsViewModeRow(
        title = stringResource(R.string.settings_ui_posts_view_mode_tags),
        value = tagsPostsViewMode,
        onChange = onTagsPostsViewMode
    )
    PostsViewModeRow(
        title = stringResource(R.string.settings_ui_posts_view_mode_search),
        value = searchPostsViewMode,
        onChange = onSearchPostsViewMode
    )
}
