package su.afk.kemonos.profile.presenter.setting.view.uiSetting.viewSettings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.presenter.setting.SettingState.Event
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.SwitchRow
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.common.SectionSpacer
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.common.SectionSpacerSmall
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.common.SettingsSectionTitle
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.postsSize.PostsSizeRow

@Composable
internal fun ViewSettingsSection(
    ui: UiSettingModel,
    onEvent: (Event) -> Unit,
) {
    SectionSpacer()
    SettingsSectionTitle(text = stringResource(R.string.settings_view_modes_section_title))
    ViewModesSection(
        creatorsViewMode = ui.creatorsViewMode,
        creatorsFavoriteViewMode = ui.creatorsFavoriteViewMode,
        profilePostsViewMode = ui.profilePostsViewMode,
        favoritePostsViewMode = ui.favoritePostsViewMode,
        popularPostsViewMode = ui.popularPostsViewMode,
        tagsPostsViewMode = ui.tagsPostsViewMode,
        searchPostsViewMode = ui.searchPostsViewMode,
        onCreatorsViewMode = { onEvent(Event.ChangeViewSetting.CreatorsViewMode(it)) },
        onCreatorsFavoriteViewMode = { onEvent(Event.ChangeViewSetting.CreatorsFavoriteViewMode(it)) },
        onProfilePostsViewMode = { onEvent(Event.ChangeViewSetting.ProfilePostsViewMode(it)) },
        onFavoritePostsViewMode = { onEvent(Event.ChangeViewSetting.FavoritePostsViewMode(it)) },
        onPopularPostsViewMode = { onEvent(Event.ChangeViewSetting.PopularPostsViewMode(it)) },
        onTagsPostsViewMode = { onEvent(Event.ChangeViewSetting.TagsPostsViewMode(it)) },
        onSearchPostsViewMode = { onEvent(Event.ChangeViewSetting.SearchPostsViewMode(it)) },
    )

    SectionSpacerSmall()
    SettingsSectionTitle(text = stringResource(R.string.settings_posts_appearance_title))
    Spacer(Modifier.height(6.dp))

    PostsSizeRow(
        title = stringResource(R.string.settings_posts_size_title),
        value = ui.postsSize,
        onChange = { onEvent(Event.ChangeViewSetting.EditPostsSize(it)) }
    )

    SwitchRow(
        title = stringResource(R.string.settings_show_preview_video_title),
        checked = ui.showPreviewVideo,
        onCheckedChange = { onEvent(Event.ChangeViewSetting.ShowPreviewVideo(it)) },
    )

    SwitchRow(
        title = stringResource(R.string.settings_blur_images_title),
        checked = ui.blurImages,
        onCheckedChange = { onEvent(Event.ChangeViewSetting.BlurImages(it)) },
    )
}
