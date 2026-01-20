package su.afk.kemonos.profile.presenter.setting.view.uiSetting

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.*
import su.afk.kemonos.profile.BuildConfig
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.presenter.setting.SettingState

@Composable
internal fun UISettingBlock(
    state: SettingState.State,
    onSkipApiCheckOnLogin: (Boolean) -> Unit,
    onSuggestRandomAuthors: (Boolean) -> Unit,
    onCreatorsViewMode: (CreatorViewMode) -> Unit,
    onCreatorsFavoriteViewMode: (CreatorViewMode) -> Unit,
    onProfilePostsViewMode: (PostsViewMode) -> Unit,
    onFavoritePostsViewMode: (PostsViewMode) -> Unit,
    onPopularPostsViewMode: (PostsViewMode) -> Unit,
    onTagsPostsViewMode: (PostsViewMode) -> Unit,
    onSearchPostsViewMode: (PostsViewMode) -> Unit,
    onTranslateTarget: (TranslateTarget) -> Unit,
    onRandomPlacement: (RandomButtonPlacement) -> Unit,
    onTranslateLanguageTag: (String) -> Unit,
    onDateFormatMode: (DateFormatMode) -> Unit,
) {
    val ui = state.uiSettingModel ?: return

    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            SettingsHeader()

            if (BuildConfig.DEBUG) {
                Spacer(Modifier.height(12.dp))
                SettingsSectionTitle(text = stringResource(R.string.settings_debug_title))
                DebugSection(
                    skipApiCheckOnLogin = ui.skipApiCheckOnLogin,
                    onSkipApiCheckOnLogin = onSkipApiCheckOnLogin,
                )
            }

            Spacer(Modifier.height(12.dp))
            SettingsSectionTitle(text = stringResource(R.string.settings_ui_general_title))
            GeneralUiSection(
                suggestRandomAuthors = ui.suggestRandomAuthors,
                onSuggestRandomAuthors = onSuggestRandomAuthors,
                dateFormatMode = ui.dateFormatMode,
                onDateFormatMode = onDateFormatMode,
            )

            Spacer(Modifier.height(12.dp))
            SettingsSectionTitle(text = stringResource(R.string.settings_translate_section_title))
            TranslateSection(
                translateTarget = ui.translateTarget,
                translateLanguageTag = ui.translateLanguageTag,
                onTranslateTarget = onTranslateTarget,
                onTranslateLanguageTag = onTranslateLanguageTag,
            )

            Spacer(Modifier.height(12.dp))
            SettingsSectionTitle(text = stringResource(R.string.settings_random_section_title))
            RandomSection(
                randomButtonPlacement = ui.randomButtonPlacement,
                onRandomPlacement = onRandomPlacement,
            )

            Spacer(Modifier.height(12.dp))
            SettingsSectionTitle(text = stringResource(R.string.settings_view_modes_section_title))
            ViewModesSection(
                creatorsViewMode = ui.creatorsViewMode,
                creatorsFavoriteViewMode = ui.creatorsFavoriteViewMode,
                profilePostsViewMode = ui.profilePostsViewMode,
                favoritePostsViewMode = ui.favoritePostsViewMode,
                popularPostsViewMode = ui.popularPostsViewMode,
                tagsPostsViewMode = ui.tagsPostsViewMode,
                searchPostsViewMode = ui.searchPostsViewMode,
                onCreatorsViewMode = onCreatorsViewMode,
                onCreatorsFavoriteViewMode = onCreatorsFavoriteViewMode,
                onProfilePostsViewMode = onProfilePostsViewMode,
                onFavoritePostsViewMode = onFavoritePostsViewMode,
                onPopularPostsViewMode = onPopularPostsViewMode,
                onTagsPostsViewMode = onTagsPostsViewMode,
                onSearchPostsViewMode = onSearchPostsViewMode,
            )
        }
    }
}

@Composable
private fun SettingsHeader() {
    Text(
        text = stringResource(R.string.settings_ui_title),
        style = MaterialTheme.typography.titleLarge
    )

    Spacer(Modifier.height(6.dp))

    Text(
        text = stringResource(R.string.settings_ui_hint),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun DebugSection(
    skipApiCheckOnLogin: Boolean,
    onSkipApiCheckOnLogin: (Boolean) -> Unit,
) {
    Spacer(Modifier.height(6.dp))
    DebugSwitchRow(
        title = stringResource(R.string.settings_debug_skip_api_check_title),
        subtitle = stringResource(R.string.settings_debug_skip_api_check_hint),
        checked = skipApiCheckOnLogin,
        onCheckedChange = onSkipApiCheckOnLogin,
    )
}

@Composable
private fun GeneralUiSection(
    suggestRandomAuthors: Boolean,
    onSuggestRandomAuthors: (Boolean) -> Unit,
    dateFormatMode: DateFormatMode,
    onDateFormatMode: (DateFormatMode) -> Unit,
) {
    Spacer(Modifier.height(6.dp))
    SwitchRow(
        title = stringResource(R.string.settings_ui_suggest_random_authors_title),
        checked = suggestRandomAuthors,
        onCheckedChange = onSuggestRandomAuthors,
    )

    Spacer(Modifier.height(8.dp))

    DateFormatRow(
        title = stringResource(R.string.settings_ui_date_format_title),
        value = dateFormatMode,
        onChange = onDateFormatMode
    )
}

@Composable
private fun TranslateSection(
    translateTarget: TranslateTarget,
    translateLanguageTag: String,
    onTranslateTarget: (TranslateTarget) -> Unit,
    onTranslateLanguageTag: (String) -> Unit,
) {
    Spacer(Modifier.height(6.dp))
    TranslateTargetRow(
        title = stringResource(R.string.settings_translate_title),
        value = translateTarget,
        onChange = onTranslateTarget
    )

    Spacer(Modifier.height(8.dp))
    TranslateLanguageRow(
        title = stringResource(R.string.settings_translate_language_title),
        languageTag = translateLanguageTag,
        onChange = onTranslateLanguageTag,
    )
}

@Composable
private fun RandomSection(
    randomButtonPlacement: RandomButtonPlacement,
    onRandomPlacement: (RandomButtonPlacement) -> Unit,
) {
    Spacer(Modifier.height(6.dp))
    RandomButtonPlacementRow(
        title = stringResource(R.string.settings_random_button_title),
        value = randomButtonPlacement,
        onChange = onRandomPlacement
    )
}

@Composable
private fun ViewModesSection(
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