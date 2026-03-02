package su.afk.kemonos.setting.presenter.view.viewSettings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.setting.R
import su.afk.kemonos.setting.presenter.SettingState.Event
import su.afk.kemonos.setting.presenter.view.SwitchRow
import su.afk.kemonos.setting.presenter.view.common.SectionSpacer
import su.afk.kemonos.setting.presenter.view.common.SectionSpacerSmall
import su.afk.kemonos.setting.presenter.view.common.SettingsSectionTitle

@Composable
internal fun ViewSettingsSection(
    ui: UiSettingModel,
    onEvent: (Event) -> Unit,
) {
    SectionSpacerSmall()
    SettingsSectionTitle(text = stringResource(R.string.settings_posts_appearance_title))
    Spacer(Modifier.height(6.dp))

    SwitchRow(
        title = stringResource(R.string.settings_show_preview_video_title),
        checked = ui.showPreviewVideo,
        onCheckedChange = { onEvent(Event.ChangeViewSetting.ShowPreviewVideo(it)) },
    )

    SwitchRow(
        title = stringResource(R.string.settings_blur_images_title),
        subtitle = stringResource(R.string.settings_blur_images_substring),
        checked = ui.blurImages,
        onCheckedChange = { onEvent(Event.ChangeViewSetting.BlurImages(it)) },
    )

    SwitchRow(
        title = stringResource(R.string.settings_show_image_preview_download_action_title),
        checked = ui.showImagePreviewDownloadAction,
        onCheckedChange = { onEvent(Event.ChangeViewSetting.ShowImagePreviewDownloadAction(it)) },
    )

    SwitchRow(
        title = stringResource(R.string.settings_show_image_preview_share_action_title),
        checked = ui.showImagePreviewShareAction,
        onCheckedChange = { onEvent(Event.ChangeViewSetting.ShowImagePreviewShareAction(it)) },
    )

    SwitchRow(
        title = stringResource(R.string.settings_show_comments_in_post_title),
        checked = ui.showCommentsInPost,
        onCheckedChange = { onEvent(Event.ChangeViewSetting.ShowCommentsInPost(it)) },
    )

    SectionSpacer()
    SettingsSectionTitle(text = stringResource(R.string.settings_view_modes_section_title))
    ViewModesSection(
        ui = ui,
        onEvent = onEvent,
    )
}
