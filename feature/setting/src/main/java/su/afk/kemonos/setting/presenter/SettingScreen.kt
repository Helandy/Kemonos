package su.afk.kemonos.setting.presenter

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.profile.R
import su.afk.kemonos.setting.presenter.SettingState.State
import su.afk.kemonos.setting.presenter.view.apiSetting.ApiSettingsBlock
import su.afk.kemonos.setting.presenter.view.bottomLink.BottomLinksBlock
import su.afk.kemonos.setting.presenter.view.cache.CacheSettingsBlock
import su.afk.kemonos.setting.presenter.view.uiSetting.UISettingBlock
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.CenterBackTopBar
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.ui.preview.KemonosPreviewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingScreen(
    state: State,
    onEvent: (SettingState.Event) -> Unit,
    effect: Flow<SettingState.Effect>
) {
    val uriHandler = LocalUriHandler.current

    BaseScreen(
        contentModifier = Modifier.padding(horizontal = 8.dp),
        isScroll = true,
        isLoading = state.loading,
        topBarScroll = TopBarScroll.ExitUntilCollapsed,
        customTopBar = { scrollBehavior ->
            CenterBackTopBar(
                title = stringResource(R.string.setting),
                onBack = { onEvent(SettingState.Event.Back) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) {
        UISettingBlock(state = state, onEvent = onEvent)

        Spacer(Modifier.height(16.dp))

        ApiSettingsBlock(
            state = state,
            onKemonoChanged = { onEvent(SettingState.Event.ApiSetting.InputKemonoDomainChanged(it)) },
            onCoomerChanged = { onEvent(SettingState.Event.ApiSetting.InputCoomerDomainChanged(it)) },
            onSave = { onEvent(SettingState.Event.ApiSetting.SaveUrls) }
        )

        Spacer(Modifier.height(16.dp))

        CacheSettingsBlock(
            state = state,
            dateFormatMode = state.uiSettingModel.dateFormatMode,
            onEvent = onEvent
        )

        Spacer(Modifier.height(16.dp))

        BottomLinksBlock(
            kemonoUrl = state.kemonoUrl,
            coomerUrl = state.coomerUrl,
            appVersion = state.appVersion,
            onGitHubClick = {
                uriHandler.openUri("https://github.com/Helandy/Kemonos")
            }
        )
    }
}

@Preview("PreviewSettingScreen")
@Composable
private fun PreviewSettingScreen() {
    KemonosPreviewScreen {
        SettingScreen(
            state = State().copy(loading = false),
            onEvent = {},
            effect = emptyFlow(),
        )
    }
}
