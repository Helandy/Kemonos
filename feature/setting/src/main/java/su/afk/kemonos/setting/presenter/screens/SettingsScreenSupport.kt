package su.afk.kemonos.setting.presenter.screens

import su.afk.kemonos.domain.SelectedSite
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.model.CacheTimeUi
import su.afk.kemonos.preferences.ui.*
import su.afk.kemonos.setting.presenter.SettingState
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.CenterBackTopBar
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.ui.preview.KemonosPreviewScreen

private const val DAY_IN_MS = 24 * 60 * 60 * 1000L
private const val PREVIEW_NOW_MS = 1_767_312_000_000L

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ModifierParameter")
@Composable
internal fun SettingsScreenScaffold(
    title: String,
    onBack: () -> Unit,
    isLoading: Boolean,
    contentModifier: Modifier = Modifier.padding(horizontal = 8.dp),
    isScroll: Boolean = true,
    topBarScroll: TopBarScroll = TopBarScroll.Pinned,
    content: @Composable ColumnScope.() -> Unit,
) {
    BaseScreen(
        contentModifier = contentModifier,
        isScroll = isScroll,
        isLoading = isLoading,
        topBarScroll = topBarScroll,
        customTopBar = { scrollBehavior ->
            CenterBackTopBar(
                title = title,
                onBack = onBack,
                scrollBehavior = scrollBehavior,
            )
        },
        content = content,
    )
}

@Composable
internal fun SettingsPreview(
    content: @Composable () -> Unit,
) {
    KemonosPreviewScreen {
        content()
    }
}

internal fun previewSettingState(
    loading: Boolean = false,
): SettingState.State {
    return SettingState.State(
        loading = loading,
        appVersion = "preview-1.0.0",
        siteUrls = mapOf(
            SelectedSite.K to "https://kemono.su",
            SelectedSite.C to "https://coomer.su",
            SelectedSite.P to "https://pawchive.pw",
        ),
        imageHostUrls = SelectedSite.entries.associateWith { "https://img.example.com" },
        fileHostUrls = SelectedSite.entries.associateWith { "https://file.example.com" },
        inputDomains = mapOf(
            SelectedSite.K to "kemono.su",
            SelectedSite.C to "coomer.su",
            SelectedSite.P to "pawchive.pw",
        ),
        inputVideoPreviewServerDomain = "kemonos.afk.su",
        saveSuccess = true,
        uiSettingModel = UiSettingModel(
            suggestRandomAuthors = true,
            translateTarget = TranslateTarget.GOOGLE,
            randomButtonPlacement = RandomButtonPlacement.SCREEN,
            translateLanguageTag = "en",
            coilCacheSizeMb = 512,
            addServiceName = true,
            downloadFolderMode = DownloadFolderMode.CREATOR_POST_ID,
            creatorProfileHiddenTabs = setOf(CreatorProfileTabKey.DMS),
            videoPreviewServerUrl = "https://kemonos.afk.su",
        ),
        tagsCache = SelectedSite.entries.associateWith {
            CacheTimeUi(
                lastMs = PREVIEW_NOW_MS - DAY_IN_MS,
                nextMs = PREVIEW_NOW_MS + 29 * DAY_IN_MS,
                isFresh = true,
            )
        },
        creatorsCache = SelectedSite.entries.associateWith {
            CacheTimeUi(
                lastMs = PREVIEW_NOW_MS - 2 * DAY_IN_MS,
                nextMs = PREVIEW_NOW_MS + 5 * DAY_IN_MS,
                isFresh = true,
            )
        },
        communityCache = CacheTimeUi(
            lastMs = PREVIEW_NOW_MS - 2 * DAY_IN_MS,
            nextMs = PREVIEW_NOW_MS + 5 * DAY_IN_MS,
            isFresh = true,
        ),
        discordCache = CacheTimeUi(
            lastMs = PREVIEW_NOW_MS - 4 * DAY_IN_MS,
            nextMs = PREVIEW_NOW_MS + 3 * DAY_IN_MS,
            isFresh = true,
        ),
        postContentsCache = CacheTimeUi(
            lastMs = PREVIEW_NOW_MS - 3 * DAY_IN_MS,
            nextMs = PREVIEW_NOW_MS + 4 * DAY_IN_MS,
            isFresh = true,
        ),
        creatorPostsCache = CacheTimeUi(
            lastMs = PREVIEW_NOW_MS - 3 * DAY_IN_MS,
            nextMs = PREVIEW_NOW_MS + 4 * DAY_IN_MS,
            isFresh = true,
        ),
        popularKemonoCache = CacheTimeUi(
            lastMs = PREVIEW_NOW_MS - 7 * DAY_IN_MS,
            nextMs = PREVIEW_NOW_MS - DAY_IN_MS,
            isFresh = false,
        ),
        favPostsKemonoCache = CacheTimeUi(
            lastMs = PREVIEW_NOW_MS - DAY_IN_MS,
            nextMs = PREVIEW_NOW_MS + 6 * DAY_IN_MS,
            isFresh = true,
        ),
        favCreatorsKemonoCache = CacheTimeUi(
            lastMs = PREVIEW_NOW_MS - DAY_IN_MS,
            nextMs = PREVIEW_NOW_MS + 6 * DAY_IN_MS,
            isFresh = true,
        ),
        creatorProfilesCache = CacheTimeUi(
            lastMs = PREVIEW_NOW_MS - 3 * DAY_IN_MS,
            nextMs = PREVIEW_NOW_MS + 4 * DAY_IN_MS,
            isFresh = true,
        ),
    )
}
