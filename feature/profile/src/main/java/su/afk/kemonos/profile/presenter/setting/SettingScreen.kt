package su.afk.kemonos.profile.presenter.setting

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.utilsUI.KemonoPreviewScreen
import su.afk.kemonos.profile.presenter.setting.SettingState.Event
import su.afk.kemonos.profile.presenter.setting.SettingState.State
import su.afk.kemonos.profile.presenter.setting.view.apiSetting.ApiSettingsBlock
import su.afk.kemonos.profile.presenter.setting.view.bottomLink.BottomLinksBlock
import su.afk.kemonos.profile.presenter.setting.view.cache.CacheSettingsBlock
import su.afk.kemonos.profile.presenter.setting.view.faq.FaqBlock
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.UISettingBlock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingScreen(state: State, onEvent: (Event) -> Unit) {
    val uriHandler = LocalUriHandler.current

    BaseScreen(
        contentModifier = Modifier.padding(horizontal = 8.dp),
        isScroll = true,
        isLoading = state.loading,
    ) {
        UISettingBlock(state = state, onEvent = onEvent)

        Spacer(Modifier.height(16.dp))

        ApiSettingsBlock(
            state = state,
            onKemonoChanged = { onEvent(Event.ApiSetting.InputKemonoDomainChanged(it)) },
            onCoomerChanged = { onEvent(Event.ApiSetting.InputCoomerDomainChanged(it)) },
            onSave = { onEvent(Event.ApiSetting.SaveUrls) }
        )

        Spacer(Modifier.height(16.dp))

        CacheSettingsBlock(
            state = state,
            dateFormatMode = state.uiSettingModel.dateFormatMode,
            onEvent = onEvent
        )

        Spacer(Modifier.height(16.dp))

        FaqBlock()

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
    KemonoPreviewScreen {
        SettingScreen(
            state = State().copy(loading = false),
            onEvent = {},
        )
    }
}